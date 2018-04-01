package io.jim.tesserapp.math

import java.lang.Math.sqrt

/**
 * A homogeneous vector, but does not store the homogeneous component, as that component is
 * implicitly given by the subclass used.
 */
abstract class Vector(components: List<Double>) : Iterable<Double>, Indexable<Vector, Double> {

    private val components = ArrayList<Double>(components)

    override operator fun iterator() = components.iterator()

    override operator fun set(index: Int, value: Double) {
        components[index] = value
    }

    override operator fun get(index: Int): Double {
        return components[index]
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Vector) return false
        return zip(other) { a, b -> a == b }.all { it }
    }

    override fun hashCode(): Int {
        return components.hashCode()
    }

    var x: Double by IndexAlias(0)
    var y: Double by IndexAlias(1)
    var z: Double by IndexAlias(2)
    var q: Double by IndexAlias(3)

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
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('(')
        components.forEach { component -> sb.append(Format.decimalFormat.format(component)).append('|') }
        sb.setCharAt(sb.length - 1, ')')
        return sb.toString()
    }

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
     * Normalize this vector.
     */
    fun normalize() {
        val l = 1.0 / length
        for (i in 0 until dimension) {
            components[i] *= l
        }
    }

    /**
     * Scales this point by [scale].
     */
    abstract operator fun times(scale: Double): Vector

    /**
     * Divides this vector through [divisor].
     */
    abstract operator fun div(divisor: Double): Vector

    /**
     * Applies the matrix [rhs] to this vector in a returned result.
     */
    abstract operator fun times(rhs: Matrix): Vector

    /**
     * Invert this vector.
     */
    abstract operator fun unaryMinus(): Vector

}
