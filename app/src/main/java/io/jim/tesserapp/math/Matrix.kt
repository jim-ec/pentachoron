package io.jim.tesserapp.math

import io.jim.tesserapp.util.RandomAccessBuffer

/**
 * A row-major matrix with [rows] rows and [cols] columns.
 * Neither [rows] nor [cols] can be smaller than 1.
 * Initially, a matrix is always an identity matrix.
 */
data class Matrix(val rows: Int, val cols: Int) {

    /**
     * Construct a quadratic matrix with [size] rows and columns.
     */
    constructor(size: Int) : this(size, size)

    companion object {

        /**
         * Construct a matrix with one row and [size] columns, suitable for representing vectors.
         */
        fun vector(size: Int) = Matrix(1, size)

    }

    /**
     * X-component when this matrix represents a vector.
     */
    var x: Float
        get() = this[0, 0]
        set(value) { this[0, 0] = value }

    /**
     * Y-component when this matrix represents a vector.
     */
    var y: Float
        get() = this[0, 1]
        set(value) { this[0, 1] = value }

    /**
     * Z-component when this matrix represents a vector.
     */
    var z: Float
        get() = this[0, 2]
        set(value) { this[0, 2] = value }

    /**
     * Q-component when this matrix represents a vector.
     */
    var q: Float
        get() = this[0, 3]
        set(value) { this[0, 3] = value }

    /**
     * W-component when this matrix represents a vector.
     */
    var w: Float
        get() = this[0, 4]
        set(value) { this[0, 4] = value }

    /**
     * Underlying float buffer. One buffer element is seen as one row.
     */
    private val floats = RandomAccessBuffer(rows, cols)

    init {
        if(rows <= 0 || cols <= 0)
            throw RuntimeException("Matrix must have non-null positive dimensions")
        identity()
    }

    /**
     * Loads the identity matrix.
     */
    fun identity() {
        forEachComponent { row, col ->
            floats[row, col] = if(row == col) 1f else 0f
        }
    }

    /**
     * Loads a complete set of floats into the matrix.
     * Each row is one float list in [rowList].
     * @throws RuntimeException If [rowList] has not [rows] rows.
     * @throws RuntimeException If any list in [rowList] has not [cols] columns.
     */
    fun load(vararg rowList: List<Float>) {
        if(rowList.size != rows)
            throw RuntimeException("Row lists must match with matrix row count $rows")

        rowList.forEachIndexed { rowIndex, row ->
            if(row.size != cols)
                throw RuntimeException("Columns per row list must match with matrix column count $cols")

            row.forEachIndexed { colIndex, coefficient ->
                this[rowIndex, colIndex] = coefficient
            }
        }
    }

    /**
     * Set the float at [row]/[col] to [value].
     */
    operator fun set(row: Int, col: Int, value: Float) {
        floats[row, col] = value
    }

    /**
     * Return the float at [row]/[col].
     */
    operator fun get(row: Int, col: Int) = floats[row, col]

    /**
     * Shortly represents this matrix as a string.
     */
    override fun toString() = "[${rows}x$cols]"

    /**
     * Prints the whole matrix into a string representation.
     */
    @Suppress("unused")
    fun joinToString() : String {
        val sb = StringBuffer()
        sb.append("[ ")
        for(row in 0 until rows) {
            for (col in 0 until cols) {
                sb.append(decimalFormat.format(this[row, col]))
                if(col < cols - 1)
                    sb.append(" , ")
                else if(row < rows - 1)
                    sb.append(" | ")
            }
        }
        sb.append(" ]")
        return sb.toString()
    }

    /**
     * Multiply this matrix with a right-hand side matrix [rhs].
     * The result is stored into [target].
     * Only multiplications of the form MxP * PxN = MxN are valid.
     */
    fun multiply(rhs: Matrix, target: Matrix) {
        if (cols != rhs.rows)
            throw RuntimeException("Cannot multiply $this and $rhs")
        if(rows != target.rows || rhs.cols != target.cols)
            throw RuntimeException("Target matrix $target is incompatible for $this * $rhs")

        target.forEachComponent { row, col ->
            var sum = 0f

            for(i in 0 until cols) {
                sum += this[row, i] * rhs[i, col]
            }

            target[row, col] = sum
        }

        /*for(row in 0 until rows) {
            for(col in 0 until rhs.cols) {
                var sum = 0f

                for(i in 0 until cols) {
                    sum += this[row, i] * rhs[i, col]
                }

                target[row, col] = sum
            }
        }*/
    }

    /**
     * Calls [f] for each coefficient.
     */
    inline fun forEachComponent(f: (row: Int, col: Int) -> Unit) {
        for(row in 0 until rows) {
            for(col in 0 until cols) {
                f(row, col)
            }
        }
    }

}