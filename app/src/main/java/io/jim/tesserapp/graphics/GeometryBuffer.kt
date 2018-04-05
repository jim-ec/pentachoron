package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.MatrixBuffer

/**
 * A geometry buffer, responsible for vertex and index data.
 */
class GeometryBuffer(private val maxModels: Int, maxVertices: Int, maxIndices: Int) {

    /**
     * Model matrix bulk buffer.
     * The global model matrices of geometry which is actually drawn occupies the front memory.
     * The rear memory section holds global model matrices of non-geometry spatial, as well as
     * local and temporary matrices (translation and rotation).
     */
    val modelMatrices = MatrixBuffer(maxModels * (1 + Spatial.MATRICES_PER_SPATIAL + 1))

    /**
     * Actual count of valid global model matrices.
     */
    var globalModelMatrixCount = 0

    private val vertexBuffer = VertexBuffer(maxVertices)
    private val indexBuffer = IndexBuffer(maxIndices)
    private val geometryRegistry = ArrayList<GeometryEntry>()

    private data class GeometryEntry(
            val geometry: Geometry,
            val indexOffset: Int,
            val indexCount: Int)

    /**
     * Bind the geometry buffers and re-instructs vertex attribute pointers of [shader].
     */
    fun bind(shader: Shader) {
        vertexBuffer.bind(shader)
        indexBuffer.bind()
    }

    /**
     * Store the geometries, whose root is denoted by [rootSpatial], into the vertex and index
     * buffers.
     */
    fun recordGeometries(rootSpatial: Spatial) {
        geometryRegistry.clear()

        indexBuffer.startRecording()

        var geometryIndex = 0
        var modelMatrixOffset = maxModels
        globalModelMatrixCount = 0

        rootSpatial.forEachRecursive { spatial ->

            // Register model matrix for this spatial:
            spatial.buffer = modelMatrices
            spatial.offset = modelMatrixOffset + 1

            if (spatial is Geometry) {

                spatial.globalModelMatrixOffset = globalModelMatrixCount++

                for (point in spatial.points) {
                    vertexBuffer.appendVertex(point, spatial.color, geometryIndex)
                }

                val offset = indexBuffer.recordGeometry(spatial)
                geometryRegistry +=
                        GeometryEntry(spatial, offset, spatial.lines.size * 2)

                geometryIndex++
            }
            else {

                spatial.globalModelMatrixOffset = modelMatrixOffset

            }

            modelMatrixOffset += Spatial.MATRICES_PER_SPATIAL + 1

        }

        indexBuffer.endRecording()
    }

    /**
     * Return the count of indices actually stored and needed to be drawn.
     */
    fun indexCount() = indexBuffer.globalIndexCounter

}
