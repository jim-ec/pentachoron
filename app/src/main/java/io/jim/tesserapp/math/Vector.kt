package io.jim.tesserapp.math

import java.lang.Math.sqrt

/**
 * A homogeneous vector, but does not store the homogeneous component.
 */
abstract class Vector(components: List<Double>) : Iterable<Double> {

    private val components = ArrayList<Double>(components)

    override operator fun iterator() = components.iterator()

    var x: Double
        get() = this[0]
        set(value) {
            this[0] = value
        }

    operator fun set(index: Int, value: Double) {
        components[index] = value
    }

    operator fun get(index: Int): Double {
        return components[index]
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Vector) return false
        return zip(other) { a, b -> a == b }.all { it }
    }

    override fun hashCode(): Int {
        return components.hashCode()
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
        get() = components.size

    /**
     * Checks whether two vector have the same count of components.
     */
    infix fun compatible(v: Vector) =
            dimension == v.dimension

    /**
     * Represent this vector as a string.
     */
    override fun toString() =
            StringBuilder().also {
                it.append('(')
                components.forEach { component -> it.append(component).append('|') }
                it.setCharAt(it.length - 1, ')')
            }.toString()

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(components.map { it * it }.sum())

    /**
     * Scalar this and [v].
     */
    operator fun times(v: Vector) =
            components.zip(v.components) { a, b -> a * b }.sum()

    /**
     * Return this vector in it's normalized form.
     */
    val normalized
        get() = this / length

    /**
     * Scales this point by [scale].
     */
    abstract operator fun times(scale: Double): Vector

    /**
     * Divides this vector through [divisor].
     */
    abstract operator fun div(divisor: Double): Vector

    abstract val homogeneous: List<Double>

    abstract operator fun times(rhs: Matrix): Vector
}
