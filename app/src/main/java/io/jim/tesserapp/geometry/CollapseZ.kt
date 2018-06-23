package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

/**
 * Project [geometry] down to a 3d wireframe.
 * @param f Called for each wireframe vertex, i.e. its position and color.
 */
inline fun collapseZ(
        geometry: Geometry,
        crossinline f: (position: VectorN, color: Geometry.Color) -> Unit) {
    
    val modelMatrix = geometry.onTransformUpdate()
    
    geometry.lines.forEach { line ->
        line.forEachPosition { position ->
            
            // Apply 4-dimensional model matrix to 4d point:
            val transformedPosition = position * modelMatrix
            
            transformedPosition.z = transformedPosition.q // Store q as z
            transformedPosition.q = 0.0
            
            f(transformedPosition, line.color)
        }
    }
}
