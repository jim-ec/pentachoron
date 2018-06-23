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
                Matrix(size, size) { row, col ->
                    if (row == a && col == a)
                        cos(radians)
                    else if (row == a && col == b)
                        sin(radians)
                    else if (row == b && col == a)
                        -sin(radians)
                    else if (row == b && col == b)
                        cos(radians)
                    else if (row == col)
                        1.0
                    else
                        0.0
                }
    }
    
    /**
     * Load a 3D to 2D perspective matrix.
     * The z component gets remapping between a near and far value.
     * @param near Near plane. If Vector lies on that plane (negated), it will be projected to 0.
     * @param far Far plane. If Vector lies on that plane (negated), it will be projected to 1.
     */
    fun perspective2D(near: Double, far: Double) {
        
        if (near <= 0.0 || far <= 0.0 || near > far)
            throw MathException("Invalid near=$near or far=$far parameter")
        
        perspective2D()
        
        this[2, 2] = -far / (far - near)
        this[3, 2] = -(far * near) / (far - near)
    }
    
    /**
     * Load a 3D to 2D perspective matrix without remapping z.
     */
    fun perspective2D() {
        this[2, 3] = -1.0
        this[3, 3] = 0.0
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
