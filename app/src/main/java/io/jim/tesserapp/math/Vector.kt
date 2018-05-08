package io.jim.tesserapp.math

import io.jim.tesserapp.math.common.formatNumber
import kotlin.math.sqrt

/**
 * 4 dimensional vector.
 */
data class Vector(

        /**
         * The X component.
         */
        var x: Float,

        /**
         * The Y component.
         */
        var y: Float,

        /**
         * The Z component.
         */
        var z: Float,

        /**
         * The W component.
         */
        var q: Float) {

    /**
     * Set the component at the given [index] to a given [value].
     */
    operator fun set(index: Int, value: Float) {
        when (index) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            3 -> q = value
            else -> throw IndexOutOfBoundsException("4D vector has no ${index}th component")
        }
    }

    /**
     * Return the component at the given [index].
     */
    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        2 -> z
        3 -> q
        else -> throw IndexOutOfBoundsException("4D vector has no ${index}th component")
    }

    /**
     * Represent this vector as a string.
     */
    override fun toString() = let {
        val sb = StringBuilder()
        sb.append('(')
        sb.append(formatNumber(x)).append('|')
        sb.append(formatNumber(y)).append('|')
        sb.append(formatNumber(z)).append('|')
        sb.append(formatNumber(q))
        sb.setCharAt(sb.length - 1, ')')
        sb.toString()
    }

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(this * this)

    /**
     * Scalar this and [v].
     */
    operator fun times(v: Vector) = x * v.x + y * v.y + z * v.z + q * v.q

    /**
     * Normalize this vector.
     */
    fun normalize() = apply {
        val oneOverLength = 1f / length
        x *= oneOverLength
        y *= oneOverLength
        z *= oneOverLength
        q *= oneOverLength
    }

    /**
     * Add [v] added to this v.
     */
    operator fun plus(v: Vector) =
            Vector(x + v.x, y + v.y, z + v.z, q + v.q)

    /**
     * Subtract [v] from this vector.
     */
    operator fun minus(v: Vector) =
            Vector(x - v.x, y - v.y, z - v.z, q - v.q)

    /**
     * Scales this by [scale].
     */
    operator fun times(scale: Float) =
            Vector(x * scale, y * scale, z * scale, q * scale)

    /**
     * Divides this vector through [divisor].
     */
    operator fun div(divisor: Float) =
            Vector(x / divisor, y / divisor, z / divisor, q / divisor)

    /**
     * Divides every component by this vector's q component.
     */
    fun perspectiveDivide() {
        val oneOverW = 1f / q
        x *= oneOverW
        y *= oneOverW
        z *= oneOverW
        q = 1f
    }

    /**
     * Compute the vector product of this direction and [v].
     * This totally ignores the q component.
     */
    infix fun cross(v: Vector) =
            Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x, 0f)

}
