package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.matrix.identity
import io.jim.tesserapp.math.vector.VectorN

/**
 * A geometrical structure consisting of vertices.
 *
 * @property isFourDimensional
 * If true, a special geometry visualization is done in order to represent the four dimensional geometry
 * in a three dimensional space.
 */
class Geometry constructor(
        val name: String,
        val onTransformUpdate: () -> Matrix = { identity(5) },
        val isFourDimensional: Boolean = false,
        val points: List<Line>
) {
    
    /**
     * Invoke [f] for each position and the color it's associated with.
     */
    inline fun forEachVertex(f: (position: VectorN, color: Color) -> Unit) {
        points.forEach {
            f(it.start, it.color)
            f(it.end, it.color)
        }
    }
    
    /**
     * Represents this geometry in a string.
     */
    override fun toString() = name
    
    /**
     * Symbolic colors.
     *
     * Geometries are colored indirectly using this palette.
     * The actual color integer is not relevant to the geometry.
     *
     * This is used to implement dynamic coloring when switching themes, without having to
     * rebuild the geometry just to change the color.
     */
    enum class Color {
        PRIMARY,
        ACCENT,
        X,
        Y,
        Z,
        Q
    }
    
}
