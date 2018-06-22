package io.jim.tesserapp.math.vector

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.common.formatNumber
import io.jim.tesserapp.math.matrix.Matrix
import kotlin.math.sqrt

/**
 * An n-dimensional vector.
 *
 * Most methods are designed to reduce allocations, and therefore usually operate on the
 * current vector object, rather than creating a new, modified one.
 * That enables you to provide pre-allocated vectors, while avoiding allocating mass of vectors
 * when doing the same calculations over and over again.
 *
 * @property dimension Dimension of this vector. Determines count of numbers [numbers] holds.
 */
class VectorN(
        val dimension: Int
) : Iterable<Double> {

    /**
     * Construct a 3d vector.
     */
    constructor(x: Double, y: Double, z: Double) : this(3) {
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * Construct a 4d vector.
     */
    constructor(x: Double, y: Double, z: Double, q: Double) : this(4) {
        this.x = x
        this.y = y
        this.z = z
        this.q = q
    }

    constructor(rhs: VectorN) : this(rhs.dimension) {
        for (i in 0 until dimension) {
            this[i] = rhs[i]
        }
    }

    /**
     * The underlying number array.
     */
    private val numbers = DoubleArray(dimension) { 0.0 }

    /**
     * X-component.
     */
    var x: Double
        get() = this[0]
        set(value) {
            this[0] = value
        }

    /**
     * Y-component.
     */
    var y: Double
        get() = this[1]
        set(value) {
            this[1] = value
        }

    /**
     * Z-component.
     */
    var z: Double
        get() = this[2]
        set(value) {
            this[2] = value
        }

    /**
     * Q-component.
     */
    var q: Double
        get() = this[3]
        set(value) {
            this[3] = value
        }

    override fun iterator() = numbers.iterator()

    /**
     * Represent this vector as a string.
     */
    override fun toString() =
            StringBuilder().also { sb ->
                sb.append("( [").append(dimension).append("d] ")
                numbers.forEachIndexed { index, number ->
                    sb.append(formatNumber(number)).append(
                            if (index != numbers.lastIndex) " | "
                            else " )"
                    )
                }
            }.toString()

    /**
     * Return the [index]th number.
     */
    operator fun get(index: Int) = numbers[index]

    /**
     * Set [index]th number to [value].
     */
    operator fun set(index: Int, value: Double) {
        numbers[index] = value
    }

    /**
     * Compute this vector's length.
     */
    inline val length
        get() = sqrt(this * this)

    /**
     * Scalar this and [rhs].
     */
    operator fun times(rhs: VectorN) = numbers.zip(rhs.numbers).sumByDouble { (a, b) -> a * b }

    /**
     * Return this vector component-wise added to [rhs].
     */
    operator fun plus(rhs: VectorN) = VectorN(this).apply {
        rhs.forEachIndexed { i, number ->
            this[i] += number
        }
    }

    /**
     * Return a vector multiplied by [factor].
     */
    operator fun times(factor: Double) = VectorN(this).apply {
        forEachIndexed { i, number ->
            this[i] = number * factor
        }
    }

    /**
     * Return a vector divided through [divisor].
     */
    operator fun div(divisor: Double) = VectorN(this).apply {
        forEachIndexed { i, number ->
            this[i] = number / divisor
        }
    }

    /**
     * Return a this vector in its normalized form.
     */
    fun normalized() = VectorN(this) / length

    /**
     * Compute the vector product of this and [rhs].
     */
    infix fun cross(rhs: VectorN) =
            VectorN(y * rhs.z - z * rhs.y,
                    z * rhs.x - x * rhs.z,
                    x * rhs.y - y * rhs.x)

    /**
     * Multiply [lhs] and [rhs] matrix storing the result in this matrix.
     *
     * @throws MathException If the dimension requirement `MxP * PxN = MxN` is not met.
     */
    fun multiplication(lhs: VectorN, rhs: Matrix) {
        if (lhs.dimension + 1 != rhs.rows)
            throw MathException("Cannot multiply $lhs * $rhs")
        if (rhs.cols != dimension + 1)
            throw MathException("Target matrix $this is incompatible for $lhs * $rhs")

        for (col in 0 until dimension) {
            this[col] = (0 until lhs.dimension).sumByDouble { i -> lhs[i] * rhs[i, col] } + rhs[dimension, col]
        }

        val w = (0 until lhs.dimension).sumByDouble { i -> lhs[i] * rhs[i, dimension] } + rhs[dimension, dimension]
        for (i in 0 until dimension) {
            this[i] /= w
        }
    }

}
