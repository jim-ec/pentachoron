package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.Matrix

/**
 * A geometry buffer, responsible for vertex and index data.
 */
class GeometryBuffer(maxModels: Int, maxVertices: Int, maxIndices: Int) {

    /**
     * Array with global model matrices.
     * These matrices are actually uploaded to the uniforms.
     */
    val globalModelMatrices = Array(maxModels) { Matrix() }

    /**
     * Actual count of valid global model matrices.
     */
    var globalModelMatrixCount = 0

    private val vertexBuffer = VertexBuffer(maxVertices)
    private val indexBuffer = IndexBuffer(maxIndices)
    private val geometryRegistry = ArrayList<GeometryEntry>()
    private val modelMatrices = Array(maxModels * (Spatial.MATRICES_PER_SPATIAL + 1)) { Matrix() }

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
        var modelMatrixOffset = 0
        globalModelMatrixCount = 0

        rootSpatial.forEachRecursive { spatial ->

            // Register model matrix for this spatial:
            spatial.buffer = modelMatrices
            spatial.offset = modelMatrixOffset + 1

            if (spatial is Geometry) {

                spatial.globalModelMatrixBuffer = globalModelMatrices
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

                spatial.globalModelMatrixBuffer = modelMatrices
                spatial.globalModelMatrixOffset = modelMatrixOffset

            }

            modelMatrixOffset += Spatial.MATRICES_PER_SPATIAL

        }

        indexBuffer.endRecording()
    }

    /**
     * Return the count of indices actually stored and needed to be drawn.
     */
    fun indexCount() = indexBuffer.globalIndexCounter

}
