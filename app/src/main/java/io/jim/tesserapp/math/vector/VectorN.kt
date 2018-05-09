package io.jim.tesserapp.math.vector

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.common.formatNumber
import io.jim.tesserapp.math.transform.MatrixMultipliable
import kotlin.math.sqrt
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A vector with [dimension] components.
 *
 * Most methods are designed to reduce allocations, and therefore usually operate on the
 * current vector object, rather than creating a new, modified one.
 * That enables you to provide pre-allocated vectors, while avoiding allocating mass of vectors
 * when doing the same calculations over and over again.
 */
open class VectorN(
        val dimension: Int
) : Iterable<Float>, MatrixMultipliable() {

    /**
     * Create a vector whose [dimension] is determined through the count of components passed
     * to [components]. Initialize the vector components with [components].
     */
    constructor(vararg components: Float) : this(components.size) {
        load(*components)
    }

    /**
     * The underlying float array.
     */
    private val floats = FloatArray(dimension) { 0f }

    /**
     * Copy contents from [rhs] into this vector.
     * The exact class of this and [rhs] doesn't play a role, no new object is created.
     * @throws IncompatibleVectorException If vector dimension differ.
     */
    fun copyFrom(rhs: VectorN) {
        if (dimension != rhs.dimension)
            throw IncompatibleVectorException(rhs)

        rhs.forEachIndexed { index, float ->
            this[index] = float
        }
    }

    /**
     * Iterates over the floats.
     */
    override fun iterator() = floats.iterator()

    /**
     * Return a string representing this vector's dimension.
     */
    open val dimensionString = "${dimension}d"

    /**
     * Represent this vector as a string.
     */
    override fun toString() = let {
        val sb = StringBuilder()
        sb.append("( [").append(dimensionString).append("] ")
        floats.forEachIndexed { index, float ->
            sb.append(formatNumber(float)).append(
                    if (index != floats.lastIndex) " | "
                    else " )"
            )
        }
        sb.toString()
    }

    /**
     * Set [index]th float to [value].
     */
    operator fun set(index: Int, value: Float) {
        if (index < 0 || index >= dimension)
            throw MathException("Cannot set ${index}th component to a $dimensionString vector")

        floats[index] = value
    }

    /**
     * Return the [index]th float.
     */
    operator fun get(index: Int) =
            if (index < 0 || index >= dimension)
                throw MathException("Cannot set ${index}th component to a $dimensionString vector")
            else floats[index]

    /**
     * Sets each vector component with the corresponding float in [components].
     * @throws MathException If count of floats passed to [components] does not match with [dimension].
     */
    fun load(vararg components: Float) {
        if (components.size != dimension)
            throw MathException("Cannot load ${components.size} components into a $dimensionString vector")

        components.forEachIndexed { index, component ->
            this[index] = component
        }
    }

    /**
     * Scalar this and [rhs].
     */
    operator fun times(rhs: io.jim.tesserapp.math.vector.VectorN): Float {
        var sum = 0f
        forEachIndexed { index, float ->
            sum += float * rhs[index]
        }
        return sum
    }

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(this * this)

    /**
     * Add [rhs] to this vector.
     * @throws IncompatibleVectorException If vector dimension differ
     */
    operator fun plusAssign(rhs: VectorN) {
        if (dimension != rhs.dimension)
            throw IncompatibleVectorException(rhs)

        rhs.forEachIndexed { index, float ->
            set(index, get(index) + float)
        }
    }

    /**
     * Subtract [rhs] from this vector.
     * @throws IncompatibleVectorException If vector dimension differ
     */
    operator fun minusAssign(rhs: VectorN) {
        if (dimension != rhs.dimension)
            throw IncompatibleVectorException(rhs)

        rhs.forEachIndexed { index, float ->
            set(index, get(index) - float)
        }
    }

    /**
     * Normalize this vector.
     */
    fun normalize() {
        val oneOverLength = 1f / length
        forEachIndexed { index, float ->
            set(index, float * oneOverLength)
        }
    }

    /**
     * Scales this by [scale].
     */
    operator fun timesAssign(scale: Float) {
        forEachIndexed { index, _ ->
            set(index, get(index) * scale)
        }
    }

    /**
     * Divides this vector through [divisor].
     */
    operator fun divAssign(divisor: Float) {
        forEachIndexed { index, _ ->
            set(index, get(index) / divisor)
        }
    }

    /**
     * Negates all components.
     */
    fun negate() {
        forEachIndexed { index, float ->
            this[index] = -float
        }
    }

    override fun set(row: Int, col: Int, value: Float) {
        set(col, value)
    }

    override fun get(row: Int, col: Int) = this[col]

    override val cols = dimension
    override val rows = 1

    /**
     * Thrown upon operations requiring two vectors to be compatible.
     */
    inner class IncompatibleVectorException(
            incompatibleVector: VectorN
    ) : MathException("$incompatibleVector is incompatible to ${this@VectorN}")

    /**
     * Associates a member with a float at a specific [index].
     * Useful for giving index components name like `x`, `y` or `z`.
     *
     * Allows reading as well as writing to components.
     */
    inner class IndexAlias(
            private val index: Int
    ) : ReadWriteProperty<VectorN, Float> {

        init {
            if (index >= dimension)
                throw MathException("Invalid index-alias #$index to a $dimensionString vector")
        }

        override fun getValue(thisRef: VectorN, property: KProperty<*>) =
                this@VectorN[index]

        override fun setValue(thisRef: VectorN, property: KProperty<*>, value: Float) {
            this@VectorN[index] = value
        }
    }

}
