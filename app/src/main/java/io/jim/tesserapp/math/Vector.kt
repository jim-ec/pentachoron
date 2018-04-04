package io.jim.tesserapp.math

import java.lang.Math.sqrt

/**
 * 4 dimensional vector.
 */
data class Vector(

        /**
         * The X component.
         */
        var x: Double,

        /**
         * The Y component.
         */
        var y: Double,

        /**
         * The Z component.
         */
        var z: Double,

        /**
         * The W component.
         */
        var w: Double)

    : Indexable<Double> {

    /**
     * Set the component at the given [index] to a given [value].
     */
    override operator fun set(index: Int, value: Double) {
        when (index) {
            0 -> x = value
            1 -> y = value
            2 -> z = value
            3 -> w = value
            else -> throw IndexOutOfBoundsException("4D vector has no ${index}th component")
        }
    }

    /**
     * Return the component at the given [index].
     */
    override operator fun get(index: Int): Double {
        return when (index) {
            0 -> x
            1 -> y
            2 -> z
            3 -> w
            else -> throw IndexOutOfBoundsException("4D vector has no ${index}th component")
        }
    }

    /**
     * Represent this vector as a string.
     */
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('(')
        sb.append(decimalFormat.format(x)).append('|')
        sb.append(decimalFormat.format(y)).append('|')
        sb.append(decimalFormat.format(z)).append('|')
        sb.append(decimalFormat.format(w))
        sb.setCharAt(sb.length - 1, ')')
        return sb.toString()
    }

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(this * this)

    /**
     * Scalar this and [v].
     */
    operator fun times(v: Vector) = x * v.x + y * v.y + z * v.z + w * v.w

    /**
     * Normalize this vector.
     */
    fun normalize() = this.apply {
        val oneOverLength = 1.0 / length
        x *= oneOverLength
        y *= oneOverLength
        z *= oneOverLength
        w *= oneOverLength
    }

    /**
     * Add [v] added to this v.
     */
    operator fun plus(v: Vector) =
            Vector(x + v.x, y + v.y, z + v.z, w + v.w)

    /**
     * Subtract [v] from this vector.
     */
    operator fun minus(v: Vector) =
            Vector(x - v.x, y - v.y, z - v.z, w - v.w)

    /**
     * Scales this by [scale].
     */
    operator fun times(scale: Double) =
            Vector(x * scale, y * scale, z * scale, w * scale)

    /**
     * Divides this vector through [divisor].
     */
    operator fun div(divisor: Double) =
            Vector(x / divisor, y / divisor, z / divisor, w / divisor)

    /**
     * Return a vector, resulting from the multiplication of this and a [rhs] matrix.
     */
    operator fun times(rhs: Matrix) = Vector(0.0, 0.0, 0.0, 0.0).also { result ->
        for (c in 0..3) {
            for (i in 0 until 4) {
                result[c] += this[i] * rhs[i][c]
            }
        }
    }

    /**
     * Divides every component by this vector's w component.
     */
    fun perspectiveDivide() {
        val oneOverW = 1.0 / w
        x *= oneOverW
        y *= oneOverW
        z *= oneOverW
        w = 1.0
    }

    /**
     * Compute the vector product of this direction and [v].
     * This totally ignores the w component.
     */
    infix fun cross(v: Vector): Vector {
        return Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x, 0.0)
    }

}
