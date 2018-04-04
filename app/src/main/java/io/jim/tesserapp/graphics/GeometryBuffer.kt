package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.Matrix
import junit.framework.Assert

/**
 * A geometry buffer, responsible for vertex and index data.
 */
class GeometryBuffer(maxIndices: Int) {

    private val vertexBuffer = VertexBuffer(maxIndices)
    private val indexBuffer = IndexBuffer(maxIndices)
    private val geometryRegistry = ArrayList<GeometryEntry>()

    private data class GeometryEntry(
            val geometry: Geometry,
            val geometryIndex: Int,
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

        rootSpatial.forEachRecursive { geometry ->
            if (geometry is Geometry) {

                for (point in geometry.points) {
                    Assert.assertEquals("All vertices must be 3D", 3, point.dimension)
                    vertexBuffer.appendVertex(point, geometry.color, geometryIndex)
                }

                val offset = indexBuffer.recordGeometry(geometry)
                geometryRegistry +=
                        GeometryEntry(geometry, geometryIndex, offset, geometry.lines.size * 2)

                geometryIndex++
            }
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
