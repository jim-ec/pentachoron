package io.jim.tesserapp.math.vector

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.common.formatNumber
import io.jim.tesserapp.math.matrix.MatrixMultipliable
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
) : MatrixMultipliable() {

    /**
     * Create a vector whose [dimension] is determined through the count of components passed
     * to [components]. Initialize the vector components with [components].
     */
    constructor(vararg components: Double) : this(components.size) {
        load(*components)
    }

    /**
     * The underlying number array.
     */
    private val numbers = DoubleArray(dimension) { 0.0 }

    /**
     * A vector can be seen as a n-column matrix.
     * This vector is considered to be homogeneous.
     */
    final override val cols = dimension + 1

    /**
     * A vector can be seen as a one-row matrix.
     */
    override val rows = 1

    /**
     * Return a string representing this vector's dimension.
     */
    val dimensionString = "${dimension}d"

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
     * Set [index]th number to [value].
     */
    operator fun set(index: Int, value: Double) {
        if (index < 0 || index >= dimension)
            throw MathException("Cannot set ${index}th component to a $dimensionString vector")

        numbers[index] = value
    }

    /**
     * Return the [index]th number.
     */
    operator fun get(index: Int) =
            if (index < 0 || index >= dimension)
                throw MathException("Cannot set ${index}th component to a $dimensionString vector")
            else numbers[index]

    /**
     * Setter used by the generic matrix-like multiplier,
     * since that has a more abstract NxM view to matrix-like objects.
     */
    override fun set(row: Int, col: Int, value: Double) {
        set(col, value)
    }

    /**
     * Getter used by the generic matrix-like multiplier,
     * since that has a more abstract NxM view to matrix-like objects.
     */
    final override fun get(row: Int, col: Int) =
            if (col < dimension)
                this[col]
            else
                1.0

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
     * Negates all components.
     */
    fun negate() {
        forEachIndexed { index, number ->
            this[index] = -number
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
     * Thrown upon operations requiring two vectors to be compatible.
     */
    inner class IncompatibleVectorException(
            incompatibleVector: VectorN
    ) : MathException("$incompatibleVector is incompatible to ${this@VectorN}")

}
