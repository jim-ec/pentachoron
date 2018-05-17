package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.vector.Vector4dh
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

    companion object {

        /**
         * The volume onto which 4 dimensional vectors are projected.
         * This value should be well chosen, as no vector should ever have such a q-value,
         * since that will lead to projection into infinity.
         */
        const val Q_PROJECTION_VOLUME = 1f

    }

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

        geometries.forEach { geometry ->
            geometry.computeModelMatrix()
        }

        vertexMemory.rewind()

        val result = Vector4dh()

        geometries.forEach { geometry ->
            geometry.forEachVertex { position, (red, green, blue) ->

                // Apply 4-dimensional model matrix to 4d point:
                result.multiplication(position, geometry.modelMatrix)

                // Project vector down to a 3d volume:
                result /= result.q + Q_PROJECTION_VOLUME

                vertexMemory.record { memory ->
                    memory.write(result.x, result.y, result.z, 1f)
                    memory.write(red, green, blue, 1f)
                }

            }
        }
    }

}
