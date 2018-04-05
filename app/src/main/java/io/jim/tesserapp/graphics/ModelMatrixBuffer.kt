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
 */
data class ModelMatrixBuffer(

        /**
         * Maximum of individual geometries registrable into this buffer.
         */
        private val maxGeometries: Int

) {

    /**
     * Buffer containing global model matrices.
     */
    val modelMatrixBuffer = MatrixBuffer(maxGeometries)

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

    private val unusedModelIndices = TreeSet<Int>().apply { addAll(0 until maxGeometries) }

    /**
     * Retains the next, smallest free model index, removing it from the list of unused indices.
     */
    private fun retainModelIndex() =
            unusedModelIndices.pollFirst() ?: throw Exception("No more free model index")

    /**
     * Releases the [index], adding it to the list if unused indices.
     * Unused indices can be retained by [retainModelIndex] at later time point.
     */
    private fun releaseModelIndex(index: Int) {
        if (!unusedModelIndices.add(index)) throw Exception("Index already retained")
    }

    /**
     * Register [geometry] into the matrix buffer.
     */
    operator fun plusAssign(geometry: Geometry) {
        if (activeGeometries >= maxGeometries)
            throw Exception("Registration exceeds model matrix capacity")

        val retainedModelIndex = retainModelIndex()
        geometry.globalMemory = modelMatrixBuffer.MemorySpace(retainedModelIndex, 1)

        greatestRetainedModelIndex = max(greatestRetainedModelIndex, retainedModelIndex)
    }

    /**
     * Unregister [geometry] from this matrix buffer.
     * Note that does not decrease [activeGeometries] in all cases, but rather fragments the
     * matrix buffer.
     * Note that [geometry] must be previously registered into this buffer with [plusAssign].
     */
    operator fun minusAssign(geometry: Geometry) {
        if (geometry.globalMemory == null)
            throw Exception("Geometry $geometry is not registered")

        if (geometry.modelIndex == greatestRetainedModelIndex) {
            // Geometry reserve the last-most matrix, so we can decrease the greatest index
            // and actually shrink the buffer:
            greatestRetainedModelIndex--
        }

        // We add the model index to the list of unused ones, releasing it from a specific geometry:
        releaseModelIndex(geometry.modelIndex)
        geometry.globalMemory = null
    }

}
