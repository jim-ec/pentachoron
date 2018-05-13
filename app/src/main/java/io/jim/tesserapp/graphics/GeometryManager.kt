package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.rendering.VertexBuffer
import io.jim.tesserapp.util.Flag
import io.jim.tesserapp.util.InputStreamBuffer
import io.jim.tesserapp.util.ListenerList

/**
 * Manages a geometry list, while providing backing buffers for vertex and matrix data.
 *
 * This geometry buffer is only responsible for raw data, without incorporating with OpenGL at all.
 */
class GeometryManager {

    /**
     * Model matrix buffer.
     * When geometries are transformed, this buffer is updated automatically.
     */
    val modelMatrixBuffer = ModelMatrixBuffer(matrixDimension = 4)

    /**
     * Vertex buffer.
     * Buffer data is updated automatically upon geometrical change.
     */
    val vertexBuffer = InputStreamBuffer(100, VertexBuffer.FLOATS_PER_VERTEX)

    /**
     * Listeners are called when the vertex buffer was rewritten and needs to be uploaded to OpenGL.
     */
    val vertexBufferRewritten = ListenerList()

    private val vertexBufferUpdateRequested = Flag(false)

    /**
     * Registers [geometry] into this manager.
     * Does nothing if [geometry] is already registered.
     */
    operator fun plusAssign(geometry: Geometry) {
        if (!(modelMatrixBuffer.register(geometry))) return

        geometry.onGeometryChangedListeners += vertexBufferUpdateRequested::set

        // Guarantee the the geometry is initially uploaded:
        vertexBufferUpdateRequested.set()
    }

    /**
     * Unregisters [geometry] into this manager.
     * Does nothing if [geometry] is not registered.
     */
    operator fun minusAssign(geometry: Geometry) {
        if (!(modelMatrixBuffer.unregister(geometry))) return

        // Unregister this geometry manager:
        geometry.onGeometryChangedListeners -= vertexBufferUpdateRequested::set

        vertexBufferUpdateRequested.set()
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
        modelMatrixBuffer.forEachVertex { position, (red, green, blue), modelIndex ->
            vertexBuffer += listOf(
                    position.x, position.y, position.z,
                    red, green, blue,
                    modelIndex.toFloat()
            )
        }

        vertexBufferRewritten.fire()

        vertexBufferUpdateRequested.unset()
    }

    /**
     * Recomputes model matrices.
     */
    fun computeModelMatrices() {
        modelMatrixBuffer.computeModelMatrices()
    }

}
