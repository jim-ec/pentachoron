package io.jim.tesserapp.math

open class Matrix {

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

    operator fun get(index: Int) = rows[index]

    infix fun compatible(rhs: Matrix) = size == rhs.size
    infix fun compatible(rhs: Vector) = size == rhs.dimension

    /**
     * Multiply this and a given right-hand-side matrix, resulting into a matrix.
     * @exception AssertionError If matrices are not of the same dimension.
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
