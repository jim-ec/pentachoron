package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.util.InputStreamMemory
import java.util.*

/**
 * Provides model indices for geometries by keeping them in a sequential list.
 */
class ModelMatrixList(

        /**
         * Granularity at which the internal model matrix memory grows.
         */
        geometryCountsGranularity: Int = 10

) {

    /**
     * Memory containing global model matrices.
     */
    val memory = InputStreamMemory(geometryCountsGranularity, 4)

    val geometries = ArrayList<Geometry>()

    /**
     * Count of matrices from start to a specific point in matrix memory, guaranteeing that all
     * model matrices are covered.
     *
     * This is not equal to the count of actual registered geometries, as the memory might be
     * fragmented, containing empty, unused matrix slots among used ones.
     */
    val activeGeometries: Int
        get() = geometries.size

    /**
     * Register [geometry] into the matrix memory.
     * @return `false` if [geometry] is already registered.
     */
    fun register(geometry: Geometry) =
            geometries.add(geometry)

    /**
     * Unregister [geometry] from this matrix memory.
     * @return `false` if [geometry] has not been registered.
     */
    fun unregister(geometry: Geometry) =
            geometries.remove(geometry)

    fun computeModelMatrices() {
        memory.finalize()

        geometries.forEach { geometry ->
            if (with(geometry.modelMatrix) { cols != 4 || rows != 4 })
                throw RuntimeException("Model matrix ${geometry.modelMatrix} must be 4x4")

            geometry.computeModelMatrix()
            geometry.modelMatrix.writeToMemory(memory)
        }
    }

    inline fun forEachVertex(f: (position: Vector3d, color: Color, modelIndex: Int) -> Unit) {
        geometries.forEachIndexed { modelIndex, geometry ->
            geometry.forEachVertex { position, color ->
                f(position, color, modelIndex)
            }
        }
    }

}
