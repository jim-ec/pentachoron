package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.common.formatNumber
import java.nio.DoubleBuffer

/**
 * A row-major matrix with [rows] rows and [cols] columns.
 */
class Matrix(val rows: Int, val cols: Int, initializer: (row: Int, col: Int) -> Double) {
    
    constructor(size: Int)
            : this(size, mapOf())
    
    constructor(rows: Int, cols: Int)
            : this(rows, cols, mapOf())
    
    constructor(size: Int, initializer: (row: Int, col: Int) -> Double)
            : this(size, size, initializer)
    
    constructor(size: Int, initialValues: Map<Pair<Int, Int>, Double>)
            : this(size, size, initialValues)
    
    constructor(rows: Int, cols: Int, initialValues: Map<Pair<Int, Int>, Double>)
            : this(rows, cols, { row, col ->
        initialValues[row to col] ?: if (row == col) 1.0 else 0.0
    })
    
    /**
     * Underlying number list.
     */
    private val coefficients = DoubleBuffer.allocate(rows * cols)
    
    init {
        if (rows <= 0 || cols <= 0)
            throw MathException("Invalid matrix dimension $this")
        
        forEachComponent { row, col ->
            coefficients.put(rowMajorIndex(row, col), initializer(row, col))
        }
    }
    
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
     * Multiply this and [rhs] matrix returning the resulting matrix.
     *
     * @throws MathException If the dimension requirement `MxP * PxN = MxN` is not met.
     */
    operator fun times(rhs: Matrix) =
            if (cols != rhs.rows)
                throw MathException("Cannot multiply $this * $rhs")
            else
                Matrix(rows, rhs.cols) { row, col ->
                    (0 until cols).sumByDouble { this[row, it] * rhs[it, col] }
                }
    
    /**
     * Transpose the matrix.
     */
    fun transposed() = Matrix(cols, rows) { row, col -> this[col, row] }
    
    /**
     * Return the float at [row]/[col].
     */
    operator fun get(row: Int, col: Int) = coefficients[rowMajorIndex(row, col)]
    
    /**
     * Return a linear row-major index referring to the cell at [row]/[col].
     */
    private fun rowMajorIndex(row: Int, col: Int) = row * cols + col
    
    /**
     * Shortly represents this matrix as a string.
     */
    override fun toString() = "[${rows}x$cols]"
    
    /**
     * Prints the whole matrix into a string representation.
     */
    @Suppress("unused")
    fun joinToString(): String {
        val sb = StringBuffer()
        sb.append("[ ")
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                sb.append(formatNumber(this[row, col]))
                if (col < cols - 1)
                    sb.append(" , ")
                else if (row < rows - 1)
                    sb.append(" | ")
            }
        }
        sb.append(" ]")
        return sb.toString()
    }
    
}
