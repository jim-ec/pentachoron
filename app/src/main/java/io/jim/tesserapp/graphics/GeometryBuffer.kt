package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.Matrix

/**
 * A geometry buffer, responsible for vertex and index data.
 */
class GeometryBuffer(maxModels: Int, maxVertices: Int, maxIndices: Int) {

    private val vertexBuffer = VertexBuffer(maxVertices)
    private val indexBuffer = IndexBuffer(maxIndices)
    private val geometryRegistry = ArrayList<GeometryEntry>()
    private val modelMatrices = Array(maxModels * Spatial.MATRICES_PER_SPATIAL) { Matrix() }

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

        rootSpatial.forEachRecursive { geometry ->

            // Register model matrix for this spatial:
            geometry.buffer = modelMatrices
            geometry.offset = modelMatrixOffset

            if (geometry is Geometry) {

                for (point in geometry.points) {
                    vertexBuffer.appendVertex(point, geometry.color, geometryIndex)
                }

                val offset = indexBuffer.recordGeometry(geometry)
                geometryRegistry +=
                        GeometryEntry(geometry, offset, geometry.lines.size * 2)

                geometryIndex++
            }

            modelMatrixOffset += Spatial.MATRICES_PER_SPATIAL
        }

        indexBuffer.endRecording()
    }

    /**
     * Call a function for each stored geometry.
     * The function [f] gets the following parameters:
     *  - geometry: The actual geometry.
     *  - indexOffset: The position within this index buffer the current geometry's indices begin.
     *  - indexCount: The count of the current geometry's indices.
     */
    fun forEachGeometry(f: (modelMatrix: Matrix) -> Unit) {
        geometryRegistry.forEach { (geometry) ->
            f(geometry.modelMatrix())
        }
    }

    /**
     * Return the count of indices actually stored and needed to be drawn.
     */
    fun indexCount() = indexBuffer.globalIndexCounter

}
