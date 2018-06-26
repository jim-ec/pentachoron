package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.matrix.Matrix
import io.jim.tesserapp.cpp.matrix.identity

/**
 * A geometrical structure consisting of vertices.
 *
 * @property isFourDimensional
 * If true, a special geometry visualization is done in order to represent the four dimensional geometry
 * in a three dimensional space.
 */
class Geometry(
        val name: String,
        val onTransformUpdate: () -> Matrix = { identity(5) },
        val isFourDimensional: Boolean = false,
        val lines: List<Line>
) {
    
    /**
     * Represents this geometry in a string.
     */
    override fun toString() = name
    
}
