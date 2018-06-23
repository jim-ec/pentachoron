package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

/**
 * Project [geometry] down to a 3d wireframe.
 * @param f Called for each wireframe vertex, i.e. its position and color.
 */
inline fun projectWireframe(
        geometry: Geometry,
        crossinline f: (position: VectorN, color: SymbolicColor) -> Unit) {
    
    val modelMatrix = geometry.onTransformUpdate()
    
    geometry.forEachVertex { position, color ->
    
            // Apply 4-dimensional model matrix to 4d point:
            val transformedPosition = position * modelMatrix
    
            f(
                    if (geometry.isFourDimensional)
                        transformedPosition / transformedPosition.q
                    else
                        transformedPosition,
                    color
            )
    }
}
