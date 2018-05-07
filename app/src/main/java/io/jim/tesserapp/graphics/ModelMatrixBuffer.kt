package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.util.RandomAccessBuffer
import java.util.*
import kotlin.math.max

/**
 * Store model matrices.
 */
data class ModelMatrixBuffer(

        /**
         * Maximum of individual geometries registrable into this buffer.
         */
        private val maxGeometries: Int,

        /**
         * Each model matrix must have this side-length.
         */
        private val matrixDimension: Int

) {

    /**
     * Buffer containing global model matrices.
     */
    val buffer = RandomAccessBuffer(maxGeometries, matrixDimension * matrixDimension)

    private val geometries = HashMap<Geometry, Int>()

    /**
     * Count of matrices from start to a specific point in matrix buffer, guaranteeing that all
     * model matrices are covered.
     *
     * This is not equal to the count of actual registered geometries, as the buffer might be
     * fragmented, containing empty, unused matrix slots among used ones.
     */
    val activeGeometries: Int
        get() = greatestUsedModelIndex + 1

    private var greatestUsedModelIndex = 0

    private val usedModelIndices = TreeSet<Int>()

    /**
     * Return the smallest available model index.
     * This automatically marks the returned index as used.
     */
    private fun takeModelIndexAndMarkAsUsed(): Int {
        for (i in 0 until maxGeometries) {
            if (!usedModelIndices.contains(i)) {
                // Found next free model index!

                // Remember that now used model index:
                usedModelIndices += i

                // Remember the greatest model index ever used:
                greatestUsedModelIndex = max(greatestUsedModelIndex, i)

                return i
            }
        }
        throw RuntimeException("No more unused model indices")
    }

    /**
     * Releases the [index], adding it to the list if unused indices.
     */
    private fun markModelIndexAsUnused(index: Int) {
        if (!usedModelIndices.contains(index))
            throw RuntimeException("Index not releasable, because not retained")

        if (index == greatestUsedModelIndex) {
            // Geometry reserve the last-most matrix, so we can decrease the greatest index
            // and actually shrink the buffer:
            greatestUsedModelIndex--
        }

        usedModelIndices -= index
    }

    /**
     * Register [geometry] into the matrix buffer.
     */
    fun register(geometry: Geometry): Boolean {
        if (activeGeometries >= maxGeometries)
            throw RuntimeException("Registration exceeds model matrix capacity")

        if (geometries.contains(geometry)) {
            // Geometry already registered:
            return false
        }

        geometries += Pair(geometry, takeModelIndexAndMarkAsUsed())

        return true
    }

    /**
     * Unregister [geometry] from this matrix buffer.
     * Note that does not decrease [activeGeometries] in all cases, but rather fragments the
     * matrix buffer.
     * Note that [geometry] must be previously registered into this buffer with [register].
     */
    fun unregister(geometry: Geometry): Boolean {

        // If geometry is not registered, we return false to indicate that:
        val modelIndex = geometries[geometry] ?: return false

        // We add the model index to the list of unused ones, releasing it from a specific geometry:
        markModelIndexAsUnused(modelIndex)

        geometries -= geometry

        return true
    }

    fun computeModelMatrices() {
        geometries.forEach { (geometry, modelIndex) ->
            if (with(geometry.modelMatrix) { cols != matrixDimension || rows != matrixDimension })
                throw RuntimeException("Model matrix ${geometry.modelMatrix} must dimension $matrixDimension")

            geometry.computeModelMatrix()
            geometry.modelMatrix.writeIntoBuffer(modelIndex, buffer)
        }
    }

    fun forEachVertex(f: (vertex: Vertex) -> Unit) {
        geometries.forEach { (geometry, modelIndex) ->
            geometry.vertices(modelIndex).forEach(f)
        }
    }

}
