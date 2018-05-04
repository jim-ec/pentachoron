package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Manages a geometry list, while providing backing buffers for vertex and matrix data.
 *
 * This geometry buffer is only responsible for raw data, without incorporating with OpenGL at all.
 */
class GeometryManager(maxGeometries: Int) {

    /**
     * Model matrix buffer.
     * When geometries are transformed, this buffer is updated automatically.
     */
    val modelMatrixBuffer = ModelMatrixBuffer(maxGeometries)

    /**
     * Vertex buffer.
     * Buffer data is updated automatically upon geometrical change.
     */
    val vertexBuffer = InputStreamBuffer(100, Vertex.COMPONENTS_PER_VERTEX)

    /**
     * List of geometries.
     */
    private val geometries = LinkedHashSet<Geometry>()

    /**
     * Set to false if vertex buffer was re-written and needs to be uploaded to the GPU.
     */
    var verticesUpdated = true

    operator fun plusAssign(geometry: Geometry) {
        if (geometries.add(geometry)) {
            // Geometry was actually added:
            modelMatrixBuffer += geometry
        }
        uploadVertexData()
        computeModelMatrices()
    }

    operator fun minusAssign(geometry: Geometry) {
        if (geometries.remove(geometry)) {
            // Geometry was actually removed:
            modelMatrixBuffer -= geometry
        }
        uploadVertexData()
        computeModelMatrices()
    }

    init {
        Geometry.onGeometryChangedListeners += ::uploadVertexData
    }

    private fun uploadVertexData() {

        // Rewrite vertex buffer:
        vertexBuffer.rewind()

        geometries.forEach { geometry ->
            geometry.vertices.also { vertices ->
                vertices.forEach { vertex ->
                    vertexBuffer += vertex.floats
                }
            }
        }

        verticesUpdated = true
    }

    /**
     * Recomputes model matrices.
     */
    fun computeModelMatrices() {
        geometries.forEach(Geometry::computeModelMatrix)
    }

}
