package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.rendering.VertexBuffer
import io.jim.tesserapp.util.Flag
import io.jim.tesserapp.util.InputStreamMemory
import io.jim.tesserapp.util.IntFloatReinterpreter
import java.util.*

/**
 * Gathers all data necessary for drawing geometry.
 * This includes vertex data as well as model matrices.
 *
 * This geometry memory is only responsible for raw data, without incorporating with GL at all.
 */
class DrawDataProvider {

    /**
     * Vertex memory.
     * Memory data is updated automatically upon geometrical change.
     */
    val vertexMemory = InputStreamMemory(100, VertexBuffer.ATTRIBUTE_COUNTS)

    /**
     * Memory model matrices.
     */
    val modelMatrixMemory = InputStreamMemory(allocationGranularity = 10, vectorsPerElement = 4)

    /**
     * Count of managed geometries.
     */
    val geometryCounts: Int
        get() = geometries.size

    /**
     * List containing all managed geometries.
     */
    private val geometries = ArrayList<Geometry>()

    /**
     * Set when vertex memory must be rewritten due to geometry registrations, unregistrations
     * or geometrical change.
     */
    private val vertexMemoryRewriteRequested = Flag(false)

    /**
     * Used to morph integers to floats, so that integer values can be used although an
     * [InputStreamMemory] uses floats.
     */
    private val intFloatReinterpreter = IntFloatReinterpreter()

    /**
     * Add [geometry] to this provider.
     * Does nothing if [geometry] has already been added.
     */
    operator fun plusAssign(geometry: Geometry) {
        if (geometries.add(geometry)) {
            // Geometry has been successfully added.

            // Listen to geometrical changes:
            geometry.onGeometryChangedListeners += vertexMemoryRewriteRequested::set

            // Since new geometry has been added, vertex memory must be rewritten
            // to include the new vertex data:
            vertexMemoryRewriteRequested.set()
        }
    }

    /**
     * Removes [geometry] from this provider.
     * Does nothing if [geometry] has not been added to this provider.
     */
    operator fun minusAssign(geometry: Geometry) {
        if (geometries.remove(geometry)) {
            // Geometry has been successfully removed.

            // Un-listen the geometry's vertex changes:
            geometry.onGeometryChangedListeners -= vertexMemoryRewriteRequested::set

            // Since geometry has been removed, vertex memory must be rewritten
            // to exclude the old vertex data:
            vertexMemoryRewriteRequested.set()
        }
    }

    /**
     * Rewrite the vertex memory if any vertex data changed.
     *
     * @return True if vertex memory was really rewritten.
     */
    fun rewriteVertexMemoryFromIfOutdated(): Boolean {

        if (!vertexMemoryRewriteRequested) {
            // No update was requested, so memory rewrite is not necessary:
            return false
        }

        // Rewrite vertex memory:
        vertexMemory.finalize()
        geometries.forEachIndexed { modelIndex, geometry ->
            geometry.forEachVertex { position, (red, green, blue) ->
                vertexMemory.record { memory ->
                    memory.write(position.x, position.y, position.z, 1f)
                    memory.write(red, green, blue, 1f)
                    memory.write(0f, 0f, 0f, intFloatReinterpreter.toFloat(modelIndex))
                }
            }
        }

        vertexMemoryRewriteRequested.unset()

        return true
    }

    /**
     * Recomputes model matrices.
     *
     * @throws RuntimeException If any model matrix is not 4x4.
     */
    fun computeModelMatrices() {
        geometries.forEach { geometry ->
            if (with(geometry.modelMatrix) { cols != 4 || rows != 4 })
                throw RuntimeException("Model matrix ${geometry.modelMatrix} must be 4x4")

            geometry.computeModelMatrix()
            geometry.modelMatrix.writeToMemory(modelMatrixMemory)
        }

        modelMatrixMemory.finalize()
    }

}
