package io.jim.tesserapp.math

import junit.framework.Assert
import java.lang.Math.*

/**
 * A homogeneous vector, but does not store the homogeneous component, as that component is
 * implicitly given by the subclass used.
 */
class Vector(components: List<Double>) : Iterable<Double>, Indexable<Double> {

    constructor(vararg components: Double) : this(components.toList())

    constructor(p: SphericalCoordinate) : this(
            cos(p.phi) * sin(p.theta) * p.r,
            sin(p.phi) * sin(p.theta) * p.r,
            cos(p.theta) * p.r)

    private val components = ArrayList<Double>(components)

    override operator fun iterator() = components.iterator()

    override operator fun set(index: Int, value: Double) {
        components[index] = value
    }

    override operator fun get(index: Int): Double {
        return components[index]
    }

    override fun equals(other: Any?): Boolean {
        if (other !is Vector) return false
        return zip(other) { a, b -> a == b }.all { it }
    }

    override fun hashCode(): Int {
        return components.hashCode()
    }

    var x: Double by IndexAlias(0)
    var y: Double by IndexAlias(1)
    var z: Double by IndexAlias(2)
    var q: Double by IndexAlias(3)

    /**
     * Return the number of components.
     */
    val dimension
        get() = components.size

    /**
     * Checks whether two vector have the same count of components.
     */
    infix fun compatible(v: Vector) =
            dimension == v.dimension

    /**
     * Represent this vector as a string.
     */
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append('(')
        components.forEach { component -> sb.append(Format.decimalFormat.format(component)).append('|') }
        sb.setCharAt(sb.length - 1, ')')
        return sb.toString()
    }

    /**
     * Compute this vector's length.
     */
    val length
        get() = sqrt(components.map { it * it }.sum())

    /**
     * Scalar this and [v].
     */
    operator fun times(v: Vector) =
            components.zip(v.components) { a, b -> a * b }.sum()

    /**
     * Normalize this vector.
     */
    fun normalize() {
        val l = 1.0 / length
        for (i in 0 until dimension) {
            components[i] *= l
        }
    }

    /**
     * Add [v] added to this v.
     */
    operator fun plus(v: Vector) =
            Vector(zip(v) { a, b -> a + b })

    /**
     * Subtract [v] from this vector.
     */
    operator fun minus(v: Vector) =
            Vector(zip(v) { a, b -> a - b })

    /**
     * Scales this by [scale].
     */
    operator fun times(scale: Double) =
            Vector(map { it * scale })

    /**
     * Divides this vector through [divisor].
     */
    operator fun div(divisor: Double) =
            Vector(map { it / divisor })

    infix fun applyDirection(rhs: Matrix) =
            Vector(ArrayList<Double>().also {
                Assert.assertTrue(rhs compatible this)
                // Ignore last matrix row, since directions are not translated:
                for (c in 0 until dimension) {
                    it.add((0 until dimension).map { i -> rhs[i][c] * this[i] }.sum())
                }
            })

    infix fun applyPoint(rhs: Matrix) =
            Vector(ArrayList<Double>().also { list ->
                Assert.assertTrue("Vector and matrix must be compatible", rhs compatible this)

                for (c in 0..dimension) {
                    list.add((0..dimension).map { i -> rhs[i][c] * if (i < dimension) this[i] else 1.0 }.sum())
                }

                // Perspective division:
                if (list.last() != 0.0) {
                    list.forEachIndexed { index, d -> list[index] = d / list.last() }
                }
                list.removeAt(dimension)
            })

    /**
     * Compute the vector product of this direction and [v].
     * Does only work if both vectors are three dimensional.
     */
    infix fun cross(v: Vector): Vector {
        Assert.assertTrue("Cross product works only in 3D", dimension >= 3 && v.dimension >= 3)
        return Vector(y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x)
    }

    /**
     * Invert this vector.
     */
    operator fun unaryMinus() {
        forEachIndexed { index, d -> this[index] = -d }
    }

}
