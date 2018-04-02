package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import junit.framework.Assert

class GeometryBuffer(maxIndices: Int) {

    private val vertexBuffer = VertexBuffer(maxIndices)
    private val indexBuffer = IndexBuffer(maxIndices)
    private val geometryRegistry = ArrayList<GeometryEntry>()

    private data class GeometryEntry(val geometry: Geometry, val indexOffset: Int, val indexCount: Int)

    fun bind(shader: Shader) {
        vertexBuffer.bind(shader)
        indexBuffer.bind()
    }

    fun recordGeometries(rootSpatial: Spatial) {
        geometryRegistry.clear()

        indexBuffer.startRecording()

        rootSpatial.forEachRecursive { geometry ->
            if (geometry is Geometry) {

                for (point in geometry.points) {
                    Assert.assertEquals("All vertices must be 3D", 3, point.dimension)
                    vertexBuffer.appendVertex(point, geometry.color)
                }

                val offset = indexBuffer.recordGeometry(geometry)
                geometryRegistry.add(GeometryEntry(geometry, offset, geometry.lines.size * 2))
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
    fun forEachGeometry(f: (geometry: Geometry, indexOffset: Int, indexCount: Int) -> Unit) {
        geometryRegistry.forEach { entry ->
            f(entry.geometry, entry.indexOffset, entry.indexCount)
        }
    }

}
