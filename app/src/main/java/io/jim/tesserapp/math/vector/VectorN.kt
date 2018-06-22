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
open class VectorN(
        val dimension: Int
) {

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

    /**
     * The underlying number array.
     */
    private val numbers = DoubleArray(dimension) { 0.0 }

    /**
     * Return a string representing this vector's dimension.
     */
    val dimensionString = "${dimension}d"

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

        for (col in 0 until dimension + 1) {
            var sum = 0.0

            for (i in 0 until lhs.dimension) {
                sum += lhs[i] * rhs[i, col]
            }
            sum += rhs[dimension, col]

            this[col] = sum
        }
    }

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

    /**
     * Represent this vector as a string.
     */
    override fun toString() =
            StringBuilder().also { sb ->
                sb.append("( [").append(dimensionString).append("] ")
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
    operator fun get(index: Int) =
            if (index < 0 || index >= dimension + 1)
                throw MathException("Cannot set ${index}th component to a $dimensionString vector")
            else if (index < dimension)
                numbers[index]
            else
                1.0

    /**
     * Set [index]th number to [value].
     */
    operator fun set(index: Int, value: Double) {
        if (index < 0 || index >= dimension + 1)
            throw MathException("Cannot set ${index}th component to a $dimensionString vector")

        if (index < dimension)
            numbers[index] = value
        else {
            for (i in 0 until dimension) {
                this[i] /= value
            }
        }
    }

    /**
     * Sets each vector component with the corresponding number in [components].
     *
     * Since that allocates a `double`-array for each call due to its variadic parameters,
     * try to avoid invoking this function frequently, or take a look at [copy].
     *
     * @throws MathException
     * If count of numbers passed to [components] is greater than [dimension].
     */
    fun load(vararg components: Double) {
        if (components.size > dimension)
            throw MathException("Cannot load ${components.size} components into a " +
                    "$dimensionString vector")

        components.forEachIndexed { index, component ->
            this[index] = component
        }
    }

    /**
     * Copy contents of [source] into this vector.
     * The actual dimensions can differ, as long as [counts] is chosen appropriately.
     *
     * @param counts
     * How many components to copy.
     * The default count will be the smaller vector length.
     * I.e. the default value guarantees that the copy operation succeeds.
     *
     * @throws MathException
     * If [counts] is greater than the dimension of this or the source vector.
     */
    fun copy(source: VectorN, counts: Int = Math.min(dimension, source.dimension)) {

        if (counts > source.dimension)
            throw MathException("Cannot copy $counts components from a " +
                    "${source.dimensionString} vector")

        if (counts > dimension)
            throw MathException("Cannot copy $counts components to a $dimensionString vector")

        for (i in 0 until counts) {
            this[i] = source[i]
        }
    }

    /**
     * Scalar this and [rhs].
     */
    operator fun times(rhs: VectorN): Double {
        var sum = 0.0
        forEachIndexed { index, number ->
            sum += number * rhs[index]
        }
        return sum
    }

    /**
     * Compute this vector's length.
     */
    inline val length
        get() = sqrt(this * this)

    /**
     * Add [rhs] to this vector.
     * @throws IncompatibleVectorException If vector dimension differ
     */
    operator fun plusAssign(rhs: VectorN) {
        if (dimension != rhs.dimension)
            throw IncompatibleVectorException(rhs)

        rhs.forEachIndexed { index, number ->
            set(index, get(index) + number)
        }
    }

    /**
     * Subtract [rhs] from this vector.
     * @throws IncompatibleVectorException If vector dimension differ
     */
    operator fun minusAssign(rhs: VectorN) {
        if (dimension != rhs.dimension)
            throw IncompatibleVectorException(rhs)

        rhs.forEachIndexed { index, number ->
            set(index, get(index) - number)
        }
    }

    /**
     * Normalize this vector.
     */
    fun normalize() {
        val oneOverLength = 1.0 / length
        forEachIndexed { index, number ->
            set(index, number * oneOverLength)
        }
    }

    /**
     * Scales this by [scale].
     */
    operator fun timesAssign(scale: Double) {
        forEachIndexed { index ->
            set(index, get(index) * scale)
        }
    }

    /**
     * Divides this vector through [divisor].
     */
    operator fun divAssign(divisor: Double) {
        forEachIndexed { index ->
            set(index, get(index) / divisor)
        }
    }

    /**
     * Call [f] for each dimension index this vector holds.
     */
    inline fun forEachIndexed(f: (index: Int) -> Unit) {
        for (i in 0 until dimension) {
            f(i)
        }
    }

    /**
     * Call [f] for each dimension index this vector holds.
     * Additionally, the corresponding number is passed to [f].
     */
    inline fun forEachIndexed(f: (index: Int, number: Double) -> Unit) {
        for (i in 0 until dimension) {
            f(i, this[i])
        }
    }

    /**
     * Compute the vector product of [lhs] and [rhs], storing the result in this vector.
     */
    open fun crossed(lhs: VectorN, rhs: VectorN) {
        x = lhs.y * rhs.z - lhs.z * rhs.y
        y = lhs.z * rhs.x - lhs.x * rhs.z
        z = lhs.x * rhs.y - lhs.y * rhs.x
    }

    /**
     * Thrown upon operations requiring two vectors to be compatible.
     */
    inner class IncompatibleVectorException(
            incompatibleVector: VectorN
    ) : MathException("$incompatibleVector is incompatible to ${this@VectorN}")

}
