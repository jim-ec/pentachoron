package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import junit.framework.Assert

class GeometryBuffer(maxIndices: Int) {

    private val vertexBuffer = VertexBuffer(maxIndices)
    val indexBuffer = IndexBuffer(maxIndices)
    private val geometryRegistry = ArrayList<GeometryEntry>()

    data class GeometryEntry(
            val geometry: Geometry,
            val geometryIndex: Int,
            val indexOffset: Int,
            val indexCount: Int)

    fun bind(shader: Shader) {
        vertexBuffer.bind(shader)
        indexBuffer.bind()
    }

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
                geometryRegistry.add(GeometryEntry(geometry, geometryIndex, offset, geometry.lines.size * 2))

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
    fun forEachGeometry(f: (entry: GeometryEntry) -> Unit) {
        geometryRegistry.forEach { entry ->
            f(entry)
        }
    }

}
