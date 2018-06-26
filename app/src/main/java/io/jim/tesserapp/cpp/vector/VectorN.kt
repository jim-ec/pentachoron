package io.jim.tesserapp.cpp.vector

import io.jim.tesserapp.cpp.matrix.Matrix
import io.jim.tesserapp.math.MathException
import io.jim.tesserapp.math.formatNumber
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
        this[0] = x
        this[1] = y
        this[2] = z
    }
    
    /**
     * Construct a 4d vector.
     */
    constructor(x: Double, y: Double, z: Double, q: Double) : this(4) {
        this[0] = x
        this[1] = y
        this[2] = z
        this[3] = q
    }
    
    /**
     * Copy constructor, cloning [rhs].
     */
    constructor(rhs: VectorN) : this(rhs.dimension) {
        for (i in 0 until dimension) {
            this[i] = rhs[i]
        }
    }
    
    /**
     * Construct vector with explicit [dimension].
     * Components are initialized through an [initializer], receiving the current component index.
     */
    constructor(dimension: Int, initializer: (index: Int) -> Double) : this(dimension) {
        for (i in 0 until dimension) {
            numbers[i] = initializer(i)
        }
    }
    
    /**
     * The underlying number array.
     */
    private val numbers = DoubleArray(dimension) { 0.0 }
    
    /**
     * X-component.
     */
    inline val x: Double
        get() = this[0]
    
    /**
     * Y-component.
     */
    inline val y: Double
        get() = this[1]
    
    /**
     * Z-component.
     */
    inline val z: Double
        get() = this[2]
    
    /**
     * Q-component.
     */
    inline val q: Double
        get() = this[3]
    
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
     * Multiply this and [rhs] matrix returning the resulting vector.
     *
     * @throws MathException If the dimension requirement `MxP * PxN = MxN` is not met.
     */
    operator fun times(rhs: Matrix): VectorN =
            if (dimension + 1 != rhs.rows || rhs.cols != dimension + 1)
                throw MathException("Target matrix $this is incompatible for $this * $rhs")
            else
                VectorN(dimension) { col ->
                    (0 until dimension).sumByDouble { i -> this[i] * rhs[i, col] } + rhs[dimension, col]
                } / ((0 until dimension).sumByDouble { i -> this[i] * rhs[i, dimension] } + rhs[dimension, dimension])
    
}
