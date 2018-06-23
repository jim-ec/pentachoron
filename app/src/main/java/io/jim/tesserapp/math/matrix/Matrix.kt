package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.common.formatNumber
import io.jim.tesserapp.math.vector.VectorN
import java.nio.DoubleBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * A row-major matrix with [rows] rows and [cols] columns.
 *
 * @constructor Construct an identity matrix.
 * @throws MathException If either [rows] or [cols] are less than 1.
 */
class Matrix(
        val rows: Int,
        val cols: Int
) {
    
    /**
     * Construct a quadratic matrix with [size] rows and columns.
     */
    constructor(size: Int) : this(size, size)
    
    /**
     * Construct a matrix with given [rows] and [cols].
     *
     * @param initializer
     * Ran for each coefficient in order to determine the initial value.
     */
    constructor(rows: Int, cols: Int, initializer: (row: Int, col: Int) -> Double) : this(rows, cols) {
        forEachComponent { row, col ->
            this[row, col] = initializer(row, col)
        }
    }
    
    constructor(rows: Int, cols: Int, initialValues: Map<Pair<Int, Int>, Double>)
            : this(rows, cols, { row, col ->
        initialValues[row to col] ?: if (row == col) 1.0 else 0.0
    })
    
    /**
     * Underlying number list.
     */
    private val doubles = DoubleBuffer.allocate(rows * cols)
    
    init {
        if (rows <= 0 || cols <= 0)
            throw MathException("Matrix must have non-null positive dimensions")
    
        // Load identity matrix:
        forEachComponent { row, col ->
            doubles.put(rowMajorIndex(row, col), if (row == col) 1.0 else 0.0)
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
    
    companion object {
        
        fun scale(size: Int, factors: VectorN) =
                if (factors.dimension != size - 1)
                    throw MathException("")
                else
                    Matrix(size, size) { row, col ->
                        if (row == size - 1 && col == size - 1) 1.0
                        else if (row == col) factors[row]
                        else 0.0
                    }
        
        fun translation(size: Int, v: VectorN) =
                if (v.dimension != size - 1)
                    throw MathException("")
                else
                    Matrix(size, size) { row, col ->
                        when (row) {
                            col -> 1.0
                            size - 1 -> v[col]
                            else -> 0.0
                        }
                    }
    
        fun rotation(size: Int, a: Int, b: Int, radians: Double) =
                Matrix(size, size, mapOf(
                        a to a to cos(radians),
                        a to b to sin(radians),
                        b to a to -sin(radians),
                        b to b to cos(radians)
                ))
    
        /**
         * Load a 3D to 2D perspective matrix.
         * The z component gets remapping between a near and far value.
         * @param near Near plane. If Vector lies on that plane (negated), it will be projected to 0.
         * @param far Far plane. If Vector lies on that plane (negated), it will be projected to 1.
         */
        fun perspective(near: Double, far: Double) =
                Matrix(4, 4, mapOf(
                        2 to 3 to -1.0,
                        3 to 3 to 0.0,
                        2 to 2 to -far / (far - near),
                        3 to 2 to -(far * near) / (far - near)
                ))
    }
    
    /**
     * Transpose the matrix.
     */
    fun transposed() = Matrix(cols, rows) { row, col -> this[col, row] }
    
    /**
     * Return a linear row-major index referring to the cell at [row]/[col].
     */
    fun rowMajorIndex(row: Int, col: Int) = row * cols + col
    
    /**
     * Set the float at [row]/[col] to [value].
     */
    operator fun set(row: Int, col: Int, value: Double) {
        doubles.put(rowMajorIndex(row, col), value)
    }
    
    /**
     * Return the float at [row]/[col].
     */
    operator fun get(row: Int, col: Int) = doubles[rowMajorIndex(row, col)]
    
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
