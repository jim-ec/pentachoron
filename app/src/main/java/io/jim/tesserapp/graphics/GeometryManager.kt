package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.util.InputStreamBuffer
import io.jim.tesserapp.util.ListenerListParam

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
     * Listeners are called when the vertex buffer was rewritten and needs to be uploaded to the GPU.
     */
    val vertexBufferRewritten = ListenerListParam<InputStreamBuffer>()

    private val geometries = LinkedHashSet<Geometry>()
    private var vertexBufferUpdateRequested = false

    private fun requestVertexBufferRewrite() {
        vertexBufferUpdateRequested = true
    }

    operator fun plusAssign(geometry: Geometry) {
        if (geometries.add(geometry)) {
            // Geometry was actually added:
            modelMatrixBuffer += geometry

            geometry.onGeometryChangedListeners += ::requestVertexBufferRewrite

            // Guarantee the the geometry is initially uploaded:
            vertexBufferUpdateRequested = true
        }
    }

    operator fun minusAssign(geometry: Geometry) {
        if (geometries.remove(geometry)) {
            // Geometry was actually removed:
            modelMatrixBuffer -= geometry

            // Unregister this geometry manager:
            geometry.onGeometryChangedListeners -= ::requestVertexBufferRewrite

            vertexBufferUpdateRequested = true
        }
    }

    /**
     * Rewrite the vertex buffer if any vertex data changed.
     */
    fun updateVertexBuffer() {

        if (!vertexBufferUpdateRequested) {
            // No update was requested, so buffer rewrite is not necessary:
            return
        }

        // Rewrite vertex buffer:
        vertexBuffer.rewind()
        geometries.forEach { geometry ->
            geometry.vertices.also { vertices ->
                vertices.forEach { vertex ->
                    vertexBuffer += vertex.floats
                }
            }
        }

        vertexBufferRewritten.fire(vertexBuffer)

        vertexBufferUpdateRequested = false
    }

    /**
     * Recomputes model matrices.
     */
    fun computeModelMatrices() {
        geometries.forEach(Geometry::computeModelMatrix)
    }

}
