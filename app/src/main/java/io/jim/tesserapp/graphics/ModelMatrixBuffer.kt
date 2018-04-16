package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.MatrixBuffer
import junit.framework.Assert.assertTrue

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
    val globalBuffer = MatrixBuffer(maxGeometries)

    /**
     * Count of active global model matrices.
     * This is lower or equal to [maxGeometries].
     */
    var activeGeometries = 0

    private val localBuffer = MatrixBuffer(maxGeometries * Geometry.LOCAL_MATRICES_PER_GEOMETRY)

    /**
     * Register [geometry] into the matrix buffer.
     */
    operator fun plusAssign(geometry: Geometry) {
        assertTrue("Registration exceeds model matrix capacity", activeGeometries < maxGeometries)
        geometry.localMemory = localBuffer.MemorySpace(activeGeometries * Geometry.LOCAL_MATRICES_PER_GEOMETRY, Geometry.LOCAL_MATRICES_PER_GEOMETRY)
        geometry.globalMemory = globalBuffer.MemorySpace(activeGeometries, 1)
        activeGeometries++
    }

}
