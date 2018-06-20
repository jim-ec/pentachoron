package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.Vector4dh

/**
 * Project [geometry] down to a 3d wireframe.
 * @param f Called for each wireframe vertex, i.e. its position and color.
 */
inline fun collapseZ(
        geometry: Geometry,
        crossinline f: (position: Vector4dh, color: Geometry.Color) -> Unit) {
    
    geometry.transform.computeModelMatrix()
    
    geometry.lines.indexedForEach { line ->
        line.forEachPosition { position ->
            
            // Apply 4-dimensional model matrix to 4d point:
            val transformedPosition = Vector4dh()
            transformedPosition.multiplication(position, geometry.transform.modelMatrix)
            
            transformedPosition.z = transformedPosition.q // Store q as z
            transformedPosition.q = 0.0
            
            f(transformedPosition, line.color)
        }
    }
}
