package io.jim.tesserapp.rendering

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.MatrixBuffer
import junit.framework.Assert.assertTrue

/**
 * A geometry buffer, responsible for vertex and index data.
 * How vertex, index and matrix data is laid out is completely up to this geometry buffer.
 */
class GeometryBufferOld(private val maxModels: Int, maxVertices: Int) {

    /**
     * Buffer storing local model matrices.
     */
    val localMatrixBuffer = MatrixBuffer(maxModels * Geometry.LOCAL_MATRICES_PER_GEOMETRY)

    /**
     * Buffer storing global model matrices.
     */
    val globalMatrixBuffer = MatrixBuffer(maxModels)

    /**
     * Actual count of valid global model matrices.
     * This is the count of matrices actually uploaded to shaders.
     */
    var activeGeometries = 0
        private set

    /**
     * Return the count of indices actually stored and needed to be drawn.
     */
    var vertexCount = 0
        private set

    private val vertexBuffer = VertexBufferBacking(maxVertices)

    /**
     * Bind the geometry buffers and re-instructs vertex attribute pointers of [shader].
     */
    fun bind(shader: Shader) {
        vertexBuffer.bind(shader)
    }

    /**
     * Store the geometries, whose root is denoted by [rootGeometry], into the vertex and index
     * buffers.
     */
    fun recordGeometries(rootGeometry: Geometry) {

        // Flush matrix buffer (We currently need to do this, since the count an therefore the
        // order of geometries could be changed):
        globalMatrixBuffer.MemorySpace().also {
            for (i in 0 until globalMatrixBuffer.maxMatrices) {
                it.identity(i)
            }
        }
        localMatrixBuffer.MemorySpace().also {
            for (i in 0 until localMatrixBuffer.maxMatrices) {
                it.identity(i)
            }
        }

        vertexCount = 0
        activeGeometries = 0

        rootGeometry.forEachRecursive {

            assertTrue("Too many geometries are recorded", activeGeometries < maxModels)

            it.localMemory = localMatrixBuffer.MemorySpace(activeGeometries * Geometry.LOCAL_MATRICES_PER_GEOMETRY, Geometry.LOCAL_MATRICES_PER_GEOMETRY)
            it.globalMemory = globalMatrixBuffer.MemorySpace(activeGeometries, 1)

            // Copy all vertices from geometry into vertex buffer:
            println("Geometry ${it.name} uploads ${it.lines.size * 2} vertices")
            for (point in it.vertexPoints) {
                vertexBuffer.appendVertex(point, it.color, activeGeometries)
                vertexCount++
            }

            activeGeometries++
        }
    }

}
