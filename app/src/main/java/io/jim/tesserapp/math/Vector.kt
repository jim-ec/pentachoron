package io.jim.tesserapp.math

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import java.lang.Math.*

class Vector : ArrayList<Double> {

    constructor(vararg components: Double) : super(components.toList())

    constructor(components: List<Double>) : super(components)

    constructor(size: Int) {
        for (i in 0 until size) {
            add(0.0)
        }
    }

    constructor(p: SphericalCoordinate) {
        add(cos(p.phi) * sin(p.theta) * p.r)
        add(sin(p.phi) * sin(p.theta) * p.r)
        add(cos(p.theta) * p.r)
    }

    companion object {

        fun point(vararg components: Double) =
                Vector(*components, 1.0)

        fun direction(vararg components: Double) =
                Vector(*components, 0.0)

    }

    var x: Double
        get() = this[0]
        set(value) {
            this[0] = value
        }

    var y: Double
        get() = this[1]
        set(value) {
            this[1] = value
        }

    var z: Double
        get() = this[2]
        set(value) {
            this[2] = value
        }

    var w: Double
        get() = this[3]
        set(value) {
            this[3] = value
        }

    /**
     * Return the number of components.
     */
    val dimension
        get() = size

    /**
     * Checks whether two vector have the same count of components.
     */
    infix fun compatible(v: Vector) =
            dimension == v.dimension

    /**
     * Checks whether this is a null vector, i.e. all components are 0.
     */
    val isNull
        get() = all { it == 0.0 }

    /**
     * Represent this vector as a string.
     */
    override fun toString() =
            StringBuilder().apply {
                append('(')
                this@Vector.forEach { append(it).append('|') }
                setCharAt(length - 1, ')')
            }.toString()

    /**
     * Return [v] added to this vector.
     */
    operator fun plus(v: Vector) =
            Vector(zip(v) { a, b -> a + b })

    /**
     * Return [v] subtracted from this vector.
     */
    operator fun minus(v: Vector) =
            Vector(zip(v) { a, b -> a - b })

    /**
     * Scalar this and [v].
     */
    operator fun times(v: Vector) =
            zip(v) { a, b -> a * b }.sum()

    /**
     * Scales this by [d].
     */
    operator fun times(d: Double) =
            Vector(map { it * d })

    /**
     * Divides this vector through [d].
     */
    operator fun div(d: Double) =
            Vector(map { it / d })

    /**
     * Compute the vector product of this vector and [v].
     * Does only work if both vectors are three dimensional.
     */
    infix fun cross(v: Vector): Vector {
        assertEquals(dimension, 3)
        assertEquals(v.dimension, 3)
        return Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    }

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(map { it * it }.sum())

    /**
     * Return this vector in it's normalized form.
     */
    val normalized
        get() = this / length

    /**
     * Project this vector orthographically into one smaller size.
     */
    val orthographicProjection
        get() = Vector(dropLast(1))

    /**
     * Project this vector into one smaller size by using perspective division
     * through the last component. All points are projected onto a single plane defined by all
     * points where the last component equals to 1.
     */
    val perspectiveProjection
        get() =
            orthographicProjection.also {
                it.forEachIndexed { i, d ->
                    it[i] = d / last()
                }
            }

    /**
     * Multiply this and a given left-hand-side vector, resulting into a vector.
     * @exception AssertionError If matrix and vector are not of the same size.
     */
    operator fun times(rhs: Matrix) =
            Vector(dimension).also {
                assertTrue(rhs compatible this)
                for (c in 0 until dimension) {
                    it[c] = (0 until dimension).map { i -> rhs[i][c] * this[i] }.sum()
                }
            }

}
