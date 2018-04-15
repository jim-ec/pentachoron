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

    private val localBuffer = MatrixBuffer(maxGeometries * Geometry.LOCAL_MATRICES_PER_GEOMETRY)
    private val globalBuffer = MatrixBuffer(maxGeometries)
    private var geometryCount = 0

    /**
     * Register [geometry] into the matrix buffer.
     */
    operator fun plusAssign(geometry: Geometry) {
        assertTrue("Registration exceeds model matrix capacity", geometryCount < maxGeometries)
        geometry.localMemory = localBuffer.MemorySpace(geometryCount * Geometry.LOCAL_MATRICES_PER_GEOMETRY, Geometry.LOCAL_MATRICES_PER_GEOMETRY)
        geometry.globalMemory = globalBuffer.MemorySpace(geometryCount, 1)
        geometryCount++
    }

}
