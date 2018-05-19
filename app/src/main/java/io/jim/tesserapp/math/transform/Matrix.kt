package io.jim.tesserapp.math.transform

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
open class Matrix(
        final override val rows: Int,
        final override val cols: Int
) : MatrixMultipliable() {

    /**
     * Underlying float buffer.
     */
    internal val doubles = DoubleBuffer.allocate(rows * cols)

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
     * Loads the identity matrix.
     */
    fun identity() {
        forEachComponent { row, col ->
            doubles.put(row * cols + col, if (row == col) 1.0 else 0.0)
        }
    }

    /**
     * Converts a row into a [VectorN].
     * The [VectorN.dimension] is determined by this matrix' column count.
     */
    fun toVector(row: Int) = VectorN(cols).also {
        for (i in 0 until it.dimension) {
            it[i] = this[row, i]
        }
    }

    /**
     * Loads a complete set of doubles into the matrix.
     *
     * @param rowVectors Lists containing doubles. Each list is considered as one matrix row.
     * @throws MathException If [rowVectors] has not [rows] rows.
     * @throws MathException If any list in [rowVectors] has not [cols] columns.
     */
    fun load(vararg rowVectors: VectorN) {
        if (rowVectors.size != rows)
            throw MathException("Row count must match with matrix row count $rows")

        rowVectors.forEachIndexed { rowIndex, row ->
            if (row.dimension != cols)
                throw MathException("Columns per row vector must match with matrix column count $cols")

            row.forEachIndexed { colIndex, coefficient ->
                this[rowIndex, colIndex] = coefficient
            }
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
     * Load a scale matrix.
     * @param factors Scale amount for each matrix axis.
     * @throws IncompatibleTransformDimension
     * @throws IsNotQuadraticException
     */
    fun scale(factors: VectorN) {
        if (rows != cols)
            throw IsNotQuadraticException()
        if (factors.dimension != cols - 1)
            throw IncompatibleTransformDimension(factors.dimension)

        for (i in 0 until cols - 1) {
            this[i, i] = factors[i]
        }
    }

    /**
     * Load a scale matrix.
     * @param factor Scale amount for each matrix axis.
     * @throws IsNotQuadraticException
     */
    fun scale(factor: Double) {
        if (rows != cols)
            throw IsNotQuadraticException()

        for (i in 0 until cols - 1) {
            this[i, i] = factor
        }
    }

    /**
     * Load a translation.
     * This does not reset any parts of the matrix,
     * just the doubles representing translation are modified.
     * @throws IncompatibleTransformDimension
     * @throws IsNotQuadraticException
     */
    fun translation(v: VectorN) {
        if (rows != cols)
            throw IsNotQuadraticException()
        if (v.dimension != cols - 1)
            throw IncompatibleTransformDimension(v.dimension)

        v.forEachIndexed { col, coefficient ->
            this[rows - 1, col] = coefficient
        }
    }

    /**
     * Load a rotation matrix.
     * The rotation takes place on the given [a]-[b]-plane, the angle is defined in [radians].
     * @throws IncompatibleTransformDimension
     */
    fun rotation(a: Int, b: Int, radians: Double) {
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
    fun transpose() {
        var tmp: Double
        forEachComponent { row, col ->
            // Do neither swap the same coefficients twice nor the pivots at all:
            if (row < col) {
                tmp = this[row, col]
                this[row, col] = this[col, row]
                this[col, row] = tmp
            }
        }
    }

    /**
     * Set the float at [row]/[col] to [value].
     */
    public override operator fun set(row: Int, col: Int, value: Double) {
        doubles.put(row * cols + col, value)
    }

    /**
     * Return the float at [row]/[col].
     */
    public override operator fun get(row: Int, col: Int) = doubles[row * cols + col]

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
