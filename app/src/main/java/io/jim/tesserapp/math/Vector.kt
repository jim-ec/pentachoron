package io.jim.tesserapp.math

import java.lang.Math.*
import java.util.*

class Vector {

    private val components: DoubleArray

    constructor(vararg components: Double) {
        this.components = DoubleArray(components.size) { components[it] }
    }

    constructor(components: List<Double>) {
        this.components = components.toDoubleArray()
    }

    constructor(size: Int, value: Double = 0.0) {
        components = DoubleArray(size) { value }
    }

    constructor(p: SphericalCoordinate) {
        components = DoubleArray(3)
        this[0] = cos(p.phi) * sin(p.theta) * p.r
        this[1] = sin(p.phi) * sin(p.theta) * p.r
        this[2] = cos(p.theta) * p.r
    }

    companion object {

        fun point(vararg components: Double) =
                Vector(*components, 1.0)

        fun direction(vararg components: Double) =
                Vector(*components, 0.0)

    }

    val x get() = this[0]
    val y get() = this[1]
    val z get() = this[2]
    val w get() = this[3]

    /**
     * Return this vector's hash code.
     */
    override fun hashCode() =
            Arrays.hashCode(components)

    /**
     * Checks whether this vector and [other] are equal in instance or in value.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Vector
        if (dimension != other.dimension) return false
        return Arrays.equals(components, other.components)
    }

    /**
     * Return the number of components.
     */
    val dimension
        get() = components.size

    /**
     * Checks whether two vector have the same count of components.
     */
    infix fun compatible(v: Vector) =
            dimension == v.dimension

    /**
     * Checks whether this is a null vector, i.e. all components are 0.
     */
    val isNull
        get() = components.all { it == 0.0 }

    /**
     * Represent this vector as a string.
     */
    override fun toString() =
            StringBuilder().apply {
                append('(')
                components.forEach { append(it).append('|') }
                setCharAt(length - 1, ')')
            }.toString()

    /**
     * Iterate over the components.
     */
    operator fun iterator() =
            components.indices.iterator()

    /**
     * Get the [index]'th element.
     */
    operator fun get(index: Int) =
            components[index]

    /**
     * Set the [index]'th element to [value].
     */
    operator fun set(index: Int, value: Double) {
        components[index] = value
    }

    /**
     * Return [v] added to this vector.
     */
    operator fun plus(v: Vector) =
            Vector(components.zip(v.components) { a, b -> a + b })

    /**
     * Return [v] subtracted from this vector.
     */
    operator fun minus(v: Vector) =
            Vector(components.zip(v.components) { a, b -> a - b })

    /**
     * Scalar this and [v].
     */
    operator fun times(v: Vector) =
            components.zip(v.components) { a, b -> a * b }.sum()

    /**
     * Scales this by [d].
     */
    operator fun times(d: Double) =
            Vector(components.map { it * d })

    /**
     * Divides this vector through [d].
     */
    operator fun div(d: Double) =
            Vector(components.map { it / d })

    /**
     * Compute the vector product of this vector and [v].
     * Does only work if both vectors are three dimensional.
     */
    infix fun cross(v: Vector): Vector {
        assert(dimension == 3)
        assert(v.dimension == 3)
        return Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    }

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(components.map { it * it }.sum())

    /**
     * Return this vector in it's normalized form.
     */
    val normalized
        get() = this / length

    /**
     * Project this vector orthographically into one smaller size.
     */
    val orthographicProjection
        get() = Vector(components.dropLast(1))

    /**
     * Project this vector into one smaller size by using perspective division
     * through the last component. All points are projected onto a single plane defined by all
     * points where the last component equals to 1.
     */
    val perspectiveProjection
        get() =
            orthographicProjection.also {
                it.components.forEachIndexed { i, d ->
                    it[i] = d / components.last()
                }
            }

    /**
     * Multiply this and a given left-hand-side vector, resulting into a vector.
     * @exception AssertionError If matrix and vector are not of the same size.
     */
    operator fun times(rhs: Matrix) =
            Vector(dimension).also {
                assert(rhs compatible this)
                for (c in 0 until dimension) {
                    it[c] = (0 until dimension).map { i -> rhs[i][c] * this[i] }.sum()
                }
            }

}
