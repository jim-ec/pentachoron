package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.rendering.VertexBuffer
import io.jim.tesserapp.util.InputStreamMemory
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
     * List containing all managed geometries.
     */
    private val geometries = ArrayList<Geometry>()

    /**
     * Add [geometry] to this provider.
     * Does nothing if [geometry] has already been added.
     */
    operator fun plusAssign(geometry: Geometry) {
        geometries.add(geometry)
    }

    /**
     * Removes [geometry] from this provider.
     * Does nothing if [geometry] has not been added to this provider.
     */
    operator fun minusAssign(geometry: Geometry) {
        geometries.remove(geometry)
    }

    /**
     * Compute new model matrices and rewrite the vertex memory.
     */
    fun updateVertices() {

        vertexMemory.rewind()

        geometries.forEach { geometry ->
            geometry.generateProjectedWireframe { position, (red, green, blue) ->
                vertexMemory.record { memory ->
                    memory.write(position.x, position.y, position.z, 1.0)
                    memory.write(red, green, blue, 1.0)
                }
            }
        }
    }

}
