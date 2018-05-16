package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.rendering.VertexBuffer
import io.jim.tesserapp.util.Flag
import io.jim.tesserapp.util.InputStreamMemory
import io.jim.tesserapp.util.IntFloatReinterpreter

/**
 * Manages a geometry list, while providing memory for vertex and matrix data.
 *
 * This geometry memory is only responsible for raw data, without incorporating with OpenGL at all.
 */
class GeometryManager {

    /**
     * Model matrix memory.
     * When geometries are transformed, this memory is updated automatically.
     */
    val modelMatrixList = ModelMatrixList()

    /**
     * Vertex memory.
     * Memory data is updated automatically upon geometrical change.
     */
    val vertexMemory = InputStreamMemory(100, VertexBuffer.ATTRIBUTE_COUNTS)

    private val vertexBufferUpdateRequested = Flag(false)

    private val intFloatReinterpreter = IntFloatReinterpreter()

    /**
     * Registers [geometry] into this manager.
     * Does nothing if [geometry] is already registered.
     */
    operator fun plusAssign(geometry: Geometry) {
        if (!(modelMatrixList.register(geometry))) return

        geometry.onGeometryChangedListeners += vertexBufferUpdateRequested::set

        // Guarantee the the geometry is initially uploaded:
        vertexBufferUpdateRequested.set()
    }

    /**
     * Unregisters [geometry] into this manager.
     * Does nothing if [geometry] is not registered.
     */
    operator fun minusAssign(geometry: Geometry) {
        if (!(modelMatrixList.unregister(geometry))) return

        // Unregister this geometry manager:
        geometry.onGeometryChangedListeners -= vertexBufferUpdateRequested::set

        vertexBufferUpdateRequested.set()
    }

    /**
     * Rewrite the vertex memory if any vertex data changed.
     *
     * @return True if vertex memory was really rewritten.
     */
    fun rewriteVertexMemoryFromIfOutdated(): Boolean {

        if (!vertexBufferUpdateRequested) {
            // No update was requested, so memory rewrite is not necessary:
            return false
        }

        // Rewrite vertex memory:
        vertexMemory.finalize()
        modelMatrixList.forEachVertex { position, (red, green, blue), modelIndex ->
            vertexMemory.record { memory ->
                memory.write(position.x, position.y, position.z, 1f)
                memory.write(red, green, blue, 1f)
                memory.write(0f, 0f, 0f, intFloatReinterpreter.toFloat(modelIndex))
            }
        }

        vertexBufferUpdateRequested.unset()

        return true
    }

    /**
     * Recomputes model matrices.
     */
    fun computeModelMatrices() {
        modelMatrixList.computeModelMatrices()
    }

}
