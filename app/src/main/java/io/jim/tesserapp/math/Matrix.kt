package io.jim.tesserapp.math

import kotlin.math.cos
import kotlin.math.sin

class Matrix private constructor(val size: Int) {

    private val rows: Array<Vector> = Array(size, { Vector(size) })

    /**
     * Construct a matrix, filled up with initial values.
     * @exception AssertionError If count of given values in [l] does not match up with the matrix [size].
     */
    constructor(size: Int, l: List<Double>) : this(size) {
        assert(size == l.size)
        l.forEachIndexed { i, v -> rows[i / size][i % size] = v }
    }

    companion object {

        /**
         * Construct the identity matrix.
         */
        fun identity(size: Int) = scale(size, 1.0)

        /**
         * Construct a matrix, representing an affine linear scaling transformation.
         */
        fun scale(size: Int, s: Double) =
                Matrix(size).apply {
                    for (i in 0 until size) {
                        this[i][i] = s
                    }
                }

        /**
         * Construct a matrix, representing an affine rotation transformation of [phi] on the [n]-[m]-plane.
         * @exception AssertionError If any rotation-plane axis is larger in dimension than the matrix itself.
         */
        fun rotation(size: Int, n: Int, m: Int, phi: Double) =
                identity(size).apply {
                    assert(n < size)
                    assert(m < size)
                    // Rotation on y-w plane:
                    //  -> y-axis rotates towards w-axis
                    //  -> w-axis rotates towards negative y-axis
                    // Myy = cos(phi)   | decreases
                    // Myw = sin(phi)   | increases
                    // Mwy = -sin(phi)  | increases, but in negative direction, since y rotates towards w
                    // Mww = cos(phi)   | decreases
                    this[n][n] = cos(phi)
                    this[n][m] = sin(phi)
                    this[m][n] = -sin(phi)
                    this[m][m] = cos(phi)
                }

        /**
         * Construct a matrix, representing an affine shearing transformation on the [n]-[m]-plane.
         * The [n]-axis stretched along the [m]-axis by [nm],
         * and the [m]-axis is stretched along the [n]-axis by [mn].
         */
        fun shear(size: Int, n: Int, nm: Double, m: Int, mn: Double) =
                identity(size).apply {
                    assert(n < size)
                    assert(m < size)
                    this[n][m] = nm
                    this[m][n] = mn
                }

        /**
         * Construct a matrix, representing an affine translation transformation by a given [v] vector.
         * Remember that vectors multiplied to this matrix must be homogeneous, their last component
         * determines whether they are transformed at all.
         * @exception AssertionError If [v]'s size is not equal to [size] less one.
         */
        fun translation(size: Int, v: Vector) =
                identity(size).apply {
                    assert(size == v.size - 1)
                    for (i in 0 until size - 1) {
                        this[size - 1][i] = v[i]
                    }
                }

    }

    operator fun get(index: Int) = rows[index]

    infix fun compatible(rhs: Matrix) = size == rhs.size
    infix fun compatible(rhs: Vector) = size == rhs.size

    /**
     * Multiply this and a given right-hand-side matrix, resulting into a matrix.
     * @exception AssertionError If matrices are not of the same size.
     */
    operator fun times(rhs: Matrix) =
            Matrix(size).also {
                assert(this compatible rhs)
                for (r in 0 until size) {
                    for (c in 0 until size) {
                        it[r][c] = (0 until size).map { i -> this[r][i] * rhs[i][c] }.sum()
                    }
                }
            }

}
