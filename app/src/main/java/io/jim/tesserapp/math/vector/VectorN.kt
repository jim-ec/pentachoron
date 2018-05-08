package io.jim.tesserapp.math.vector

import io.jim.tesserapp.math.MathException
import io.jim.tesserapp.math.formatNumber
import kotlin.math.sqrt
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A vector with immutable components.
 */
open class VectorN(
        private val dimension: Int
) : Iterable<Float> {

    /**
     * Thrown upon operations requiring two vectors to be compatible.
     */
    inner class IncompatibleVectorException(
            incompatibleVector: io.jim.tesserapp.math.vector.VectorN
    ) : MathException("$incompatibleVector is incompatible to ${this@VectorN}")

    /**
     * The underlying float array.
     */
    private val floats = FloatArray(dimension) { 0f }

    /**
     * Iterates over the floats.
     */
    override fun iterator() = floats.iterator()

    protected val dimensionString: String
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
            throw MathException("Cannot set ${index}th component to a ${dimensionString} vector")

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
                throw RuntimeException("Invalid index-alias #$index to a $dimensionString vector")
        }

        override fun getValue(thisRef: VectorN, property: KProperty<*>) =
                this@VectorN[index]

        override fun setValue(thisRef: VectorN, property: KProperty<*>, value: Float) {
            this@VectorN[index] = value
        }
    }

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
     * @throws VectorN.IncompatibleVectorException If vector dimension differ
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
     * @throws VectorN.IncompatibleVectorException If vector dimension differ
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

}
