package io.jim.tesserapp.math

import junit.framework.Assert
import junit.framework.Assert.assertEquals
import java.lang.Math.cos
import java.lang.Math.sin

class Direction(components: List<Double>) : Vector(components) {

    override val homogeneous: List<Double>
        get() = ArrayList<Double>(this.toList()).apply { add(0.0) }

    constructor(vararg components: Double) : this(components.toList())

    constructor(p: SphericalCoordinate) : this(
            cos(p.phi) * sin(p.theta) * p.r,
            sin(p.phi) * sin(p.theta) * p.r,
            cos(p.theta) * p.r)

    /**
     * Add [direction] added to this direction.
     */
    operator fun plus(direction: Direction) =
            Direction(zip(direction) { a, b -> a + b })

    /**
     * Subtract [direction] from this vector.
     */
    operator fun minus(direction: Direction) =
            Direction(zip(direction) { a, b -> a - b })

    /**
     * Scales this by [scale].
     */
    override operator fun times(scale: Double) =
            Direction(map { it * scale })

    /**
     * Divides this vector through [divisor].
     */
    override operator fun div(divisor: Double) =
            Direction(map { it / divisor })

    /**
     * Multiply this and a given left-hand-side vector, resulting into a vector.
     * @exception AssertionError If matrix and vector are not of the same size.
     */
    override operator fun times(rhs: Matrix) =
            Direction(ArrayList<Double>().also {
                Assert.assertTrue(rhs compatible this)
                // Ignore last matrix row, since directions are not translated:
                for (c in 0 until dimension) {
                    it.add((0 until dimension).map { i -> rhs[i][c] * this[i] }.sum())
                }
            })

    /**
     * Compute the vector product of this vector and [v].
     * Does only work if both vectors are three dimensional.
     */
    infix fun cross(v: Vector): Vector {
        assertEquals(dimension, 3)
        assertEquals(v.dimension, 3)
        return Direction(listOf(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x))
    }

}