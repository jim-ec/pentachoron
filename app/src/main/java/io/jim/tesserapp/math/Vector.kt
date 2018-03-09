package io.jim.tesserapp.math

import java.lang.Math.sqrt
import java.util.*

open class Vector(private val components: DoubleArray) {

    /**
     * Create a vector with components taken from [l].
     */
    constructor(l: List<Double>)
            : this(l.toDoubleArray())

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
        if (size != other.size) return false
        return Arrays.equals(components, other.components)
    }

    /**
     * Return the number of components.
     */
    val size
        get() = components.size

    /**
     * Checks whether two vector have the same count of components.
     */
    infix fun compatible(v: Vector) =
            size == v.size

    /**
     * Checks whether this is a null vector, i.e. all components are 0.
     */
    val isNull
        get() = components.all { d -> d == 0.0 }

    /**
     * Represent this vector as a string.
     */
    override fun toString() =
            StringBuilder().apply {
                append('(')
                components.forEach { d -> append(d).append('|') }
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
            Vector(components.zip(v.components, { a, b -> a + b }))

    /**
     * Return [v] subtracted from this vector.
     */
    operator fun minus(v: Vector) =
            Vector(components.zip(v.components, { a, b -> a - b }))

    /**
     * Scalar this and [v].
     */
    operator fun times(v: Vector) =
            components.zip(v.components, { a, b -> a * b }).sum()

    /**
     * Scales this by [d].
     */
    operator fun times(d: Double) =
            Vector(components.map({ it -> it * d }))

    /**
     * Divides this vector through [d].
     */
    operator fun div(d: Double) =
            Vector(components.map({ it -> it / d }))

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(components.map { it -> it * it }.sum())

    /**
     * Return this vector in it's normalized form.
     */
    val normalized
        get() = this / length

    /**
     * Project this vector orthographically into one smaller dimension.
     */
    open val orthographicProjection
        get() = Vector(components.dropLast(1))

    /**
     * Project this vector into one smaller dimension by using perspective division
     * through the last component. All points are projected onto a single plane defined by all
     * points where the last component equals to [projectionPlane].
     */
    infix fun perspectiveProjection(projectionPlane: Double) =
            orthographicProjection.also {
                it.components.forEachIndexed { i, d ->
                    it[i] = (d * projectionPlane) / components.last()
                }
            }

}
