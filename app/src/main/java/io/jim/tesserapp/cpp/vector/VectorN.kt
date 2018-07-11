package io.jim.tesserapp.cpp.vector

import io.jim.tesserapp.cpp.matrix.Matrix
import kotlin.math.sqrt

class VectorN(
        val dimension: Int
) {
    
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
            components[i] = initializer(i)
        }
    }
    
    /**
     * The underlying number array.
     */
    val components = DoubleArray(dimension)
    
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
    
    /**
     * Return the [index]th number.
     */
    operator fun get(index: Int) = components[index]
    
    /**
     * Set [index]th number to [value].
     */
    operator fun set(index: Int, value: Double) {
        components[index] = value
    }
    
    /**
     * Compute this vector's length.
     */
    inline val length
        get() = sqrt(this * this)
    
    /**
     * Scalar this and [rhs].
     */
    operator fun times(rhs: VectorN) = components.zip(rhs.components).sumByDouble { (a, b) -> a * b }
    
    /**
     * Return this vector component-wise added to [rhs].
     */
    operator fun plus(rhs: VectorN) = VectorN(this).apply {
        rhs.components.forEachIndexed { i, number ->
            this[i] += number
        }
    }
    
    /**
     * Return a vector multiplied by [factor].
     */
    operator fun times(factor: Double) = VectorN(this).apply {
        components.forEachIndexed { i, number ->
            this[i] = number * factor
        }
    }
    
    /**
     * Return a vector divided through [divisor].
     */
    operator fun div(divisor: Double) = VectorN(this).apply {
        components.forEachIndexed { i, number ->
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
     * @throws RuntimeException If the dimension requirement `MxP * PxN = MxN` is not met.
     */
    operator fun times(rhs: Matrix): VectorN =
            if (dimension + 1 != rhs.rows || rhs.cols != dimension + 1)
                throw RuntimeException("Cannot multiply vector with matrix")
            else
                VectorN(dimension) { col ->
                    (0 until dimension).sumByDouble { i -> this[i] * rhs[i, col] } + rhs[dimension, col]
                } / ((0 until dimension).sumByDouble { i -> this[i] * rhs[i, dimension] } + rhs[dimension, dimension])
    
}