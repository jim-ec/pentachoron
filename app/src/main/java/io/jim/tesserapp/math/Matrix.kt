package io.jim.tesserapp.math

import io.jim.tesserapp.util.RandomAccessBuffer
import kotlin.math.cos
import kotlin.math.sin

/**
 * A row-major matrix with [rows] rows and [cols] columns.
 *
 * @constructor Construct an identity matrix.
 * @throws MathException If either [rows] or [cols] are less than 1.
 */
data class Matrix(val rows: Int, val cols: Int) {

    /**
     * Underlying float buffer. One buffer element is seen as one row.
     */
    private val floats = RandomAccessBuffer(rows, cols)

    /**
     * Construct a quadratic matrix with [size] rows and columns.
     */
    constructor(size: Int) : this(size, size)

    init {
        if (rows <= 0 || cols <= 0)
            throw MathException("Matrix must have non-null positive dimensions")
        identity()
    }

    /**
     * The element size a buffer must provide to be capable of serving as a write target.
     * @see writeIntoBuffer
     */
    val bufferElementSize = rows * cols

    /**
     * X-component when this matrix represents a vector.
     */
    var x: Float
        get() = this[0, 0]
        set(value) {
            this[0, 0] = value
        }

    /**
     * Y-component when this matrix represents a vector.
     */
    var y: Float
        get() = this[0, 1]
        set(value) {
            this[0, 1] = value
        }

    /**
     * Z-component when this matrix represents a vector.
     */
    var z: Float
        get() = this[0, 2]
        set(value) {
            this[0, 2] = value
        }

    /**
     * Q-component when this matrix represents a vector.
     */
    var q: Float
        get() = this[0, 3]
        set(value) {
            this[0, 3] = value
        }

    /**
     * W-component when this matrix represents a vector.
     */
    var w: Float
        get() = this[0, 4]
        set(value) {
            this[0, 4] = value
        }

    /**
     * Loads the identity matrix.
     */
    fun identity() {
        forEachComponent { row, col ->
            floats[row, col] = if (row == col) 1f else 0f
        }
    }

    companion object {

        /**
         * Construct a matrix with one row and [size] columns, suitable for representing vectors.
         */
        fun vector(size: Int) = Matrix(1, size)

        /**
         * Construct a matrix with one row and n columns, suitable for representing vectors.
         * The count of columns is determined by the count of numbers passed to [coefficients].
         *
         * @param coefficients Coefficients to initialize the vector with.
         */
        fun vector(vararg coefficients: Float) = vector(coefficients.size).apply {
            load(*coefficients)
        }

    }

    /**
     * Loads a complete set of floats into the matrix.
     *
     * @param rowList Lists containing floats. Each list is considered as one matrix row.
     * @throws MathException If [rowList] has not [rows] rows.
     * @throws MathException If any list in [rowList] has not [cols] columns.
     */
    fun load(vararg rowList: List<Float>) {
        if (rowList.size != rows)
            throw MathException("Row lists must match with matrix row count $rows")

        rowList.forEachIndexed { rowIndex, row ->
            if (row.size != cols)
                throw MathException("Columns per row list must match with matrix column count $cols")

            row.forEachIndexed { colIndex, coefficient ->
                this[rowIndex, colIndex] = coefficient
            }
        }
    }

    /**
     * Loads a complete set of floats into a vector matrix.
     * @param coefficients Coefficients to load into the matrix.
     * @throws MathException If this matrix is not a vector, meaning that is has more than one row.
     * @throws MathException If coefficient counts does not match the matrix column counts.
     */
    fun load(vararg coefficients: Float) {
        if (rows != 1)
            throw MathException("Matrix must be a vector")
        if (coefficients.size != cols)
            throw MathException("Coefficient count must match with matrix columns")

        coefficients.forEachIndexed { index, coefficient ->
            this[0, index] = coefficient
        }
    }

    /**
     * Thrown when an operation's requirement to be called on a quadratic matrix is not met.
     */
    inner class IsNotQuadraticException :
            MathException("Matrix needs to be quadratic but is ${this@Matrix}")

    /**
     * Thrown when a transform is incompatible with a matrix in terms of dimension.
     * @param transformDimension The incompatible transform dimension.
     */
    inner class IncompatibleTransformDimension(transformDimension: Int) :
            MathException("${transformDimension}d transform not compatible with $this")

    /**
     * Load a translation.
     * This does not reset any parts of the matrix,
     * just the floats representing translation are modified.
     * @throws IncompatibleTransformDimension
     * @throws IsNotQuadraticException
     */
    fun translation(vararg coefficients: Float) {
        if (rows != cols)
            throw IsNotQuadraticException()
        if (coefficients.size != cols - 1)
            throw IncompatibleTransformDimension(coefficients.size)

        coefficients.forEachIndexed { col, coefficient ->
            this[rows - 1, col] = coefficient
        }
    }

    /**
     * Load a rotation matrix.
     * The rotation takes place on the given [a]-[b]-plane, the angle is defined in [radians].
     * @throws IncompatibleTransformDimension
     */
    fun rotation(a: Int, b: Int, radians: Float) {
        if (a + 1 >= rows || a + 1 >= cols)
            throw IncompatibleTransformDimension(a + 1)
        if (b + 1 >= rows || b + 1 >= cols)
            throw IncompatibleTransformDimension(b + 1)

        this[a, a] = cos(radians)
        this[a, b] = sin(radians)
        this[b, a] = -sin(radians)
        this[b, b] = cos(radians)
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

    /**
     * Multiply [lhs] and [rhs] matrix storing the result in this matrix.
     *
     * @throws MathException If the dimension requirement `MxP * PxN = MxN` is not met.
     */
    fun multiplication(lhs: Matrix, rhs: Matrix) {
        if (lhs.cols != rhs.rows)
            throw MathException("Cannot multiply $lhs * $rhs")
        if (lhs.rows != rows || rhs.cols != cols)
            throw MathException("Target matrix $this is incompatible for $lhs * $rhs")

        forEachComponent { row, col ->
            var sum = 0f

            for (i in 0 until lhs.cols) {
                sum += lhs[row, i] * rhs[i, col]
            }

            this[row, col] = sum
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
     * Writes this matrix into a float buffer.
     * One element in [buffer] is considered as one matrix.
     */
    fun writeIntoBuffer(matrixOffset: Int, buffer: RandomAccessBuffer) {
        if (bufferElementSize != buffer.elementSize)
            throw MathException("Cannot write $this into buffer, wrong element size ${buffer.elementSize}")

        forEachComponent { row, col ->
            // Unlike floats, storing one element per row, the buffer stores one element per matrix:
            buffer[matrixOffset, row * cols + col] = floats[row, col]
        }
    }

}
