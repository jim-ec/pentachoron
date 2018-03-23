package io.jim.tesserapp.math

import kotlin.math.cos
import kotlin.math.sin

class Matrix {

    val size: Int
    private val rows: Array<Vector>

    constructor(size: Int) {
        assert(size > 0) { "Size must be greater than 0" }
        this.size = size
        this.rows = Array(size) {
            Vector(size).apply {
                this[it] = 1.0
            }
        }
    }

    /**
     * Construct a matrix, filled up with initial values.
     * @exception AssertionError If count of given values in [l] does not match up with the matrix [size].
     */
    constructor(size: Int, l: List<Double>) : this(size) {
        assert(size * size == l.size)
        l.forEachIndexed { i, v -> rows[i / size][i % size] = v }
    }

    companion object {

        fun space(dimension: Int, axises: List<Vector>) =
                Matrix(dimension).apply {
                    assert(axises.size + 1 == dimension) { "Axis count ${axises.size} does not match up with matrix size $dimension" }
                    assert(axises.all { it.dimension + 1 == dimension }) { "All vectors must have one component less than the matrix size $dimension" }
                    axises.forEachIndexed { r, axis ->
                        for (c in 0 until dimension - 1) {
                            this[r][c] = axis[c]
                        }
                    }
                }

        /**
         * Construct a matrix, representing an affine linear scaling transformation.
         */
        fun scale(dimension: Int, factor: Double) =
                Matrix(dimension).apply {
                    for (i in 0 until dimension - 1) {
                        this[i][i] = factor
                    }
                }

        /**
         * Construct a matrix, representing an affine rotation transformation of [phi] on the [n]-[m]-plane.
         * @exception AssertionError If any rotation-plane axis is larger in size than the matrix itself.
         */
        fun rotation(dimension: Int, n: Int, m: Int, phi: Double) =
                Matrix(dimension).apply {
                    assert(n < dimension) { "Plane-axis $n not in size $dimension" }
                    assert(m < dimension) { "Plane-axis $m not in size $dimension" }
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
        fun shear(dimension: Int, n: Int, nm: Double, m: Int, mn: Double) =
                Matrix(dimension).apply {
                    assert(n < dimension) { "Axis $n not in size $dimension" }
                    assert(m < dimension) { "Axis $m not in size $dimension" }
                    this[n][m] = nm
                    this[m][n] = mn
                }

        /**
         * Construct a matrix, representing an affine translation transformation by a given [t] vector.
         * Remember that vectors multiplied to this matrix must be homogeneous, their last component
         * determines whether they are transformed at all.
         * @exception AssertionError If [t]'s size is not equal to [size] less one.
         */
        fun translation(dimension: Int, t: Vector) =
                Matrix(dimension).apply {
                    assert(dimension == t.dimension + 1) { "Translation size ${t.dimension} does not match up with matrix size $dimension" }
                    for (i in 0 until dimension - 1) {
                        this[dimension - 1][i] = t[i]
                    }
                }
    }

    operator fun get(index: Int) = rows[index]

    infix fun compatible(rhs: Matrix) = size == rhs.size
    infix fun compatible(rhs: Vector) = size == rhs.dimension

    /**
     * Multiply this and a given right-hand-side matrix, resulting into a matrix.
     * @exception AssertionError If matrices are not of the same size.
     */
    operator fun times(rhs: Matrix) =
            Matrix(size).also {
                assert(this compatible rhs)
                forEachCoefficient { r, c ->
                    it[r][c] = (0 until size).map { i -> this[r][i] * rhs[i][c] }.sum()
                }
            }

    /**
     * Construct a matrix representing a transpose of this matrix.
     */
    fun transposed() =
            Matrix(size).also {
                forEachCoefficient { r, c -> it[r][c] = this[c][r] }
            }

    /**
     * Call a function for each coefficient. Indices of row and column are passed to [f].
     */
    fun forEachCoefficient(f: (Int, Int) -> Unit) {
        for (r in 0 until size) {
            for (c in 0 until size) {
                f(r, c)
            }
        }
    }

}
