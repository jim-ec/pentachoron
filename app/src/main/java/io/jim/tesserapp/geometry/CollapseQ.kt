package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.Vector3dh
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * Responsible for projecting 4d geometries into a 3d space.
 */
class CollapseQ {
    
    val transformedPosition = Vector4dh()
    val homogeneous = Vector3dh()
    
    /**
     * Project [geometry] down to a 3d wireframe.
     * @param f Called for each wireframe vertex, i.e. its position and color.
     */
    inline operator fun invoke(
            geometry: Geometry,
            crossinline f: (position: Vector3dh, color: Geometry.Color) -> Unit) {
        
        geometry.transform.computeModelMatrix()
        
        geometry.lines.indexedForEach { line ->
            line.forEachPosition { position ->
                
                // Apply 4-dimensional model matrix to 4d point:
                transformedPosition.multiplication(position, geometry.transform.modelMatrix)
                
                homogeneous.x = transformedPosition.x
                homogeneous.y = transformedPosition.y
                homogeneous.z = transformedPosition.q // Store q as z
                
                f(homogeneous, line.color)
            }
        }
    }
    
}
