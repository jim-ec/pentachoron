package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.MatrixBuffer

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
     * Count of active global model matrices.
     * This is lower or equal to [maxGeometries].
     */
    var activeGeometries = 0

    /**
     * Register [geometry] into the matrix buffer.
     */
    operator fun plusAssign(geometry: Geometry) {
        if (activeGeometries >= maxGeometries)
            throw Exception("Registration exceeds model matrix capacity")

        geometry.globalMemory = modelMatrixBuffer.MemorySpace(activeGeometries, 1)

        activeGeometries++
    }

    /**
     * Unregister [geometry] from this matrix buffer.
     */
    operator fun minusAssign(geometry: Geometry) {
        if (geometry.globalMemory != null)
            throw Exception("Geometry $geometry already registered")

        TODO("Implement geometry removal: maybe keep matrix?")
    }

}
