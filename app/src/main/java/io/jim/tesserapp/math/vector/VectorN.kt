package io.jim.tesserapp.math.vector

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.common.formatNumber
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
        private val dimension: Int
) : Iterable<Float> {

    /**
     * The underlying float array.
     */
    private val floats = FloatArray(dimension) { 0f }

    /*fun multiplied(lhs: VectorN, rhs: Matrix) {
        if(dimension != lhs.dimension || dimension != rhs.rows) {}
    }*/

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
    val dimensionString: String
        get() = "${dimension}d"

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
     * Scalar this and [rhs].
     */
    operator fun times(rhs: io.jim.tesserapp.math.vector.VectorN) =
            reduceIndexed { index, acc, float -> acc + float * rhs[index] }

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
            set(index, get(index) + float)
        }
    }

    /**
     * Normalize this vector.
     */
    fun normalize() = apply {
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
            set(index, get(index) * divisor)
        }
    }

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
