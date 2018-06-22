package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.common.MathException

/**
 * A mathematical object which can be used for matrix multiplications.
 * These are matrices and vectors.
 */
abstract class MatrixMultipliable {
    
    /**
     * Rows taken account into matrix multiplication.
     */
    abstract val rows: Int
    
    /**
     * Columns taken account into matrix multiplication.
     */
    abstract val cols: Int
    
    /**
     * Set value at a specific matrix cell to [value].
     */
    protected abstract operator fun set(row: Int, col: Int, value: Double)
    
    /**
     * Get value at a specific matrix cell.
     */
    abstract operator fun get(row: Int, col: Int): Double
    
    /**
     * Calls [f] for each coefficient.
     */
    inline fun forEachComponent(f: (row: Int, col: Int) -> Unit) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                f(row, col)
            }
        }
    }

    /**
     * Multiply [lhs] and [rhs] matrix storing the result in this matrix.
     *
     * @throws MathException If the dimension requirement `MxP * PxN = MxN` is not met.
     */
    fun multiplication(lhs: MatrixMultipliable, rhs: MatrixMultipliable) {
        if (lhs.cols != rhs.rows)
            throw MathException("Cannot multiply $lhs * $rhs")
        if (lhs.rows != rows || rhs.cols != cols)
            throw MathException("Target matrix $this is incompatible for $lhs * $rhs")

        forEachComponent { row, col ->
            var sum = 0.0

            for (i in 0 until lhs.cols) {
                sum += lhs[row, i] * rhs[i, col]
            }

            this[row, col] = sum
        }
    }
    
}
