package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.rendering.VertexBuffer
import io.jim.tesserapp.util.Flag
import io.jim.tesserapp.util.InputStreamBuffer
import io.jim.tesserapp.util.IntFloatReinterpreter

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
    val modelMatrixBuffer = ModelMatrixList()

    /**
     * Vertex buffer.
     * Buffer data is updated automatically upon geometrical change.
     */
    val backingVertexBuffer = InputStreamBuffer(100, VertexBuffer.ATTRIBUTE_COUNTS)

    private val vertexBufferUpdateRequested = Flag(false)

    private val intFloatReinterpreter = IntFloatReinterpreter()

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
    fun updateVertexBuffer(): Boolean {

        if (!vertexBufferUpdateRequested) {
            // No update was requested, so buffer rewrite is not necessary:
            return false
        }

        // Rewrite vertex buffer:
        backingVertexBuffer.rewind()
        modelMatrixBuffer.forEachVertex { position, (red, green, blue), modelIndex ->
            /*
            backingVertexBuffer += listOf(
                    position.x, position.y, position.z, 1f,
                    red, green, blue, 1f,
                    0f, 0f, 0f, intFloatReinterpreter.toFloat(modelIndex)
            )
            */
            backingVertexBuffer.record { buffer ->
                buffer.write(position.x, position.y, position.z, 1f)
                buffer.write(red, green, blue, 1f)
                buffer.write(0f, 0f, 0f, intFloatReinterpreter.toFloat(modelIndex))
            }
        }

        vertexBufferUpdateRequested.unset()

        return true
    }

    /**
     * Recomputes model matrices.
     */
    fun computeModelMatrices() {
        modelMatrixBuffer.computeModelMatrices()
    }

}
