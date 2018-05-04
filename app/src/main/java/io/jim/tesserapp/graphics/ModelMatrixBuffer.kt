package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.MatrixBuffer
import java.util.*
import kotlin.math.max

/**
 * Store model matrices.
 *
 * Register a geometry into this buffer associates the geometry with an own memory space,
 * which is never changed, unless the geometry is unregistered.
 *
 * You can iterate over the registered geometries with [iterator].
 */
data class ModelMatrixBuffer(

        /**
         * Maximum of individual geometries registrable into this buffer.
         */
        private val maxGeometries: Int

) : Iterable<Geometry> {

    /**
     * Iterates over the registered geometries.
     */
    override fun iterator() = geometries.iterator()

    /**
     * Buffer containing global model matrices.
     */
    val modelMatrixBuffer = MatrixBuffer(maxGeometries)

    private val geometries = HashSet<Geometry>()

    /**
     * Count of matrices from start to a specific point in matrix buffer, guaranteeing that all
     * model matrices are covered.
     *
     * This is not equal to the count of actual registered geometries, as the buffer might be
     * fragmented, containing empty, unused matrix slots among used ones.
     */
    val activeGeometries: Int
        get() = greatestRetainedModelIndex + 1

    private var greatestRetainedModelIndex = 0

    private val retainedModelIndices = TreeSet<Int>()

    /**
     * Return the smallest available model index.
     * This automatically marks the returned index as used.
     */
    private fun retainModelIndex(): Int {
        for (i in 0 until maxGeometries) {
            if (!retainedModelIndices.contains(i)) {
                // Found next free model index!
                retainedModelIndices += i
                return i
            }
        }
        throw RuntimeException("No more unused model indices")
    }

    /**
     * Releases the [index], adding it to the list if unused indices.
     * Unused indices can be retained by [retainModelIndex] at later time point.
     */
    private fun releaseModelIndex(index: Int) {
        if (!retainedModelIndices.remove(index))
            throw RuntimeException("Index not releasable, because not retained")
    }

    /**
     * Register [geometry] into the matrix buffer.
     */
    fun register(geometry: Geometry): Boolean {
        if (activeGeometries >= maxGeometries)
            throw RuntimeException("Registration exceeds model matrix capacity")

        if (!geometries.add(geometry)) {
            // Geometry already registered:
            return false
        }

        val retainedModelIndex = retainModelIndex()
        geometry.globalMemory = modelMatrixBuffer.MemorySpace(retainedModelIndex, 1)

        greatestRetainedModelIndex = max(greatestRetainedModelIndex, retainedModelIndex)

        return true
    }

    /**
     * Unregister [geometry] from this matrix buffer.
     * Note that does not decrease [activeGeometries] in all cases, but rather fragments the
     * matrix buffer.
     * Note that [geometry] must be previously registered into this buffer with [register].
     */
    fun unregister(geometry: Geometry): Boolean {
        if (geometry.globalMemory == null)
            throw RuntimeException("Geometry $geometry is not registered")

        if (!geometries.remove(geometry)) {
            // Geometry not registered:
            return false
        }

        if (geometry.modelIndex == greatestRetainedModelIndex) {
            // Geometry reserve the last-most matrix, so we can decrease the greatest index
            // and actually shrink the buffer:
            greatestRetainedModelIndex--
        }

        // We add the model index to the list of unused ones, releasing it from a specific geometry:
        releaseModelIndex(geometry.modelIndex)
        geometry.globalMemory = null

        return true
    }

}
