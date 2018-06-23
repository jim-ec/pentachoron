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
class Geometry(
        val name: String,
        val onTransformUpdate: () -> Matrix = { identity(5) },
        val isFourDimensional: Boolean = false
) {
    
    val points = ArrayList<Line>()
    
    /**
     * Add a quadrilateral with four corner and an optional color.
     * @param a First corner.
     * @param b Second corner.
     * @param c Third corner.
     * @param d Fourth corner.
     * @param color Color of added lines.
     */
    fun addQuadrilateral(
            a: VectorN,
            b: VectorN,
            c: VectorN,
            d: VectorN,
            color: Color = Color.PRIMARY
    ) {
        points += Line(a, b, color)
        points += Line(b, c, color)
        points += Line(c, d, color)
        points += Line(d, a, color)
    }
    
    ///**
    // * Extrudes the whole geometry in the given [direction].
    // * This works by duplicating the whole geometry and then connecting all point duplicate
    // * counterparts.
    // * @param keepColors The generated copy will have matching colors to the line set it originated from.
    // * @param connectorColor Color of the lines connecting the original and generated lines.
    // */
    /*fun extrude(
            direction: VectorN,
            keepColors: Boolean = false,
            connectorColor: Color = Color.PRIMARY
    ) {
        val size = positions.size
        
        for (i in 0 until size) {
            positions += positions[i] + direction
        }
        
        lines += lines.map {
            Line(
                    positions,
                    it.startIndex + size,
                    it.endIndex + size,
                    if (keepColors) it.color else Color.PRIMARY
            )
        }
        for (i in 0 until size) {
            addLine(i, i + size, connectorColor)
        }
    }*/
    
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
