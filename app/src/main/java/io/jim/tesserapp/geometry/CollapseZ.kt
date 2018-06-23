package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

/**
 * Project [geometry] down to a 3d wireframe.
 * @param f Called for each wireframe vertex, i.e. its position and color.
 */
inline fun collapseZ(
        geometry: Geometry,
        crossinline f: (position: VectorN, color: SymbolicColor) -> Unit) {
    
    val modelMatrix = geometry.onTransformUpdate()
    
    geometry.forEachVertex { position, color ->
        f(with(position * modelMatrix) { VectorN(x, y, q, 0.0) }, color)
    }
}
