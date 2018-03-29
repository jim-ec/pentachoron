package io.jim.tesserapp.math

import java.lang.Math.sqrt
import kotlin.reflect.KProperty

/**
 * A homogeneous vector, but does not store the homogeneous component, as that component is
 * implicitly given by the subclass used.
 */
abstract class Vector(components: List<Double>) : Iterable<Double> {

    private val components = ArrayList<Double>(components)

    override operator fun iterator() = components.iterator()

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

    var x: Double by Delegate(0)
    var y: Double by Delegate(1)
    var z: Double by Delegate(2)
    var q: Double by Delegate(3)

    /**
     * Enables property aliasing to components with a specific [index],
     * so 'x' aliases to the first component and so on.
     */
    private class Delegate(private val index: Int) {

        operator fun getValue(thisRef: Vector?, property: KProperty<*>): Double {
            return thisRef?.get(index)!!
        }

        operator fun setValue(thisRef: Vector?, property: KProperty<*>, value: Double) {
            thisRef?.set(index, value)
        }

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

    abstract operator fun times(rhs: Matrix): Vector
}
