package io.jim.tesserapp.cpp.matrix

/**
 * A row-major matrix with [rows] rows and [cols] columns.
 *
 * @constructor
 * Create a matrix, while initializing every coefficient.
 *
 * @param initializer
 * Called for each coefficient with the given row and column index.
 * The returned value represents the value for this specific matrix cell.
 */
class Matrix(val rows: Int, val cols: Int, initializer: (row: Int, col: Int) -> Double) {
    
    /**
     * Underlying number list.
     */
    private val coefficients = DoubleArray(rows * cols)
    
    init {
        if (rows <= 0 || cols <= 0)
            throw RuntimeException("Invalid matrix dimension")
        
        forEachComponent { row, col ->
            coefficients[rowMajorIndex(row, col)] = initializer(row, col)
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
     * Transpose the matrix.
     */
    fun transposed() = Matrix(cols, rows) { row, col -> this[col, row] }
    
    /**
     * Return the float at [row]/[col].
     */
    operator fun get(row: Int, col: Int) = coefficients[rowMajorIndex(row, col)]
    
    /**
     * Create a float array containing all matrix coefficients in row major order.
     */
    fun toFloatArray() = FloatArray(rows * cols) { index -> coefficients[index].toFloat() }
    
    /**
     * Return a linear row-major index referring to the cell at [row]/[col].
     */
    private fun rowMajorIndex(row: Int, col: Int) = row * cols + col
    
}
