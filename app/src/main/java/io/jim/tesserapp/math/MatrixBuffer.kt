package io.jim.tesserapp.math

import junit.framework.Assert
import kotlin.math.cos
import kotlin.math.sin

/**
 * A bulk buffer storing 4x4 matrices as raw floats.
 */
data class MatrixBuffer(private val maxMatrices: Int) {

    companion object {

        /**
         * Columns per matrix.
         */
        const val MATRIX_COLUMNS = 4

        /**
         * Floats per matrix.
         */
        const val MATRIX_COMPONENTS = 16

        /**
         * Row index of right vector, relative to matrix offset.
         */
        const val RIGHT_ROW = 0

        /**
         * Row index of up vector, relative to matrix offset.
         */
        const val UP_ROW = 1

        /**
         * Row index of forward vector, relative to matrix offset.
         */
        const val FORWARD_ROW = 2

        /**
         * Row index of base vector, relative to matrix offset.
         */
        const val BASE_ROW = 3
    }

    private val buffer = FloatArray(maxMatrices * MATRIX_COMPONENTS)

    init {
        // Initialize all matrices to identity matrices:
        forEachMatrix { matrix ->
            identity(matrix)
        }
    }

    private fun forEachMatrix(f: (matrix: Int) -> Unit) {
        for (i in 0 until maxMatrices) {
            f(i)
        }
    }

    private fun forEachColumn(f: (row: Int) -> Unit) {
        for (i in 0 until MATRIX_COLUMNS) {
            f(i)
        }
    }

    /**
     * Return the float at [matrix], [row] and [column].
     */
    operator fun get(matrix: Int, row: Int, column: Int) =
            buffer[matrix * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + column]

    /**
     * Set the float at [matrix], [row] and [column] to [value].
     */
    private operator fun set(matrix: Int, row: Int, column: Int, value: Float) {
        buffer[matrix * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + column] = value
    }

    /**
     * Return the vector at [matrix] and [row].
     * Allocates a new Vector object.
     */
    operator fun get(matrix: Int, row: Int) =
            Vector(
                    buffer[matrix * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 0].toDouble(),
                    buffer[matrix * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 1].toDouble(),
                    buffer[matrix * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 2].toDouble(),
                    buffer[matrix * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 3].toDouble()
            )

    /**
     * Set the vector at [matrix], in [row] to [value].
     */
    operator fun set(matrix: Int, row: Int, value: Vector) {
        for (i in 0 until MATRIX_COLUMNS) {
            buffer[matrix * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + i] = value[i].toFloat()
        }
    }

    /**
     * Load the identity matrix.
     */
    private fun identity(matrix: Int) {
        forEachColumn { row ->
            this[matrix, row, row] = 1.0f
        }
    }

    /**
     * Load a space-defining matrix into [matrix].
     */
    fun space(matrix: Int, right: Vector, up: Vector, forward: Vector, base: Vector) {
        this[matrix, RIGHT_ROW] = right
        this[matrix, UP_ROW] = up
        this[matrix, FORWARD_ROW] = forward
        this[matrix, BASE_ROW] = base
    }

    /**
     * Load a scale matrix into [matrix].
     */
    fun scale(matrix: Int, v: Vector) {
        forEachColumn { i ->
            this[matrix, i, i] = v[i].toFloat()
        }
    }

    /**
     * Load a rotation matrix into [matrix].
     * The rotation takes place on the given [a]-[b]-plane, the angle is defined in [radians].
     */
    fun rotation(matrix: Int, a: Int, b: Int, radians: Double) {
        this[matrix, a, a] = cos(radians).toFloat()
        this[matrix, a, b] = sin(radians).toFloat()
        this[matrix, b, a] = -sin(radians).toFloat()
        this[matrix, b, b] = cos(radians).toFloat()
    }

    /**
     * Load a translation matrix into [matrix].
     */
    fun translation(matrix: Int, v: Vector) {
        this[matrix, BASE_ROW] = v
    }

    /**
     * Load a 3D to 2D perspective matrix into [matrix].
     */
    fun perspective(matrix: Int) {
        this[matrix, 2, 3] = -1.0f
        this[matrix, 3, 3] = 0.0f
    }

    /**
     * Load a 3D to 2D perspective matrix into [matrix].
     * The z component gets remapping between a near and far value.
     * @param near Near plane. If Vector lies on that plane (negated), it will be projected to 0.
     * @param far Far plane. If Vector lies on that plane (negated), it will be projected to 1.
     */
    fun perspective(matrix: Int, near: Float, far: Float) {
        perspective(matrix)
        Assert.assertTrue(near > 0.0)
        Assert.assertTrue(far > 0.0)
        Assert.assertTrue(far > near)
        this[matrix, 2, 2] = -far / (far - near)
        this[matrix, 3, 2] = -(far * near) / (far - near)
    }

    /**
     * Load a look at matrix into [matrix].
     * The camera is constructed in such a way that it is positioned at [eye], Vectors to [target],
     * and the upper edge is oriented in the [refUp] Vector.
     * @param eye Eye position. W component should be 1.
     * @param target Target position. W component should be 1.
     * @param refUp Target position. W component should be 0.
     */
    fun lookAt(matrix: Int, eye: Vector, target: Vector, refUp: Vector) = this.apply {

        val forward = (eye - target).normalize()
        val right = (refUp cross forward).normalize()

        this[matrix, FORWARD_ROW] = forward
        this[matrix, RIGHT_ROW] = right
        this[matrix, UP_ROW] = (forward cross right).normalize()

        transpose(matrix)

        this[matrix, BASE_ROW] = multiply(Vector(-eye.x, -eye.y, -eye.z, eye.w), matrix)
    }

    /**
     * Multiply the two matrices [lhs] and [rhs] into [matrix].
     */
    fun multiply(lhs: Int, rhs: Int, matrix: Int) {
        var sum: Float
        forEachColumn { r ->
            forEachColumn { c ->
                sum = 0f
                forEachColumn { i ->
                    sum += this[lhs, r, i] * this[rhs, i, c]
                }
                this[matrix, r, c] = sum
            }
        }
    }

    /**
     * Multiply the vector [lhs] and the matrix [rhs] and stores the result in [result].
     * If [result] is null, it will be allocated.
     * @return The result vector, which is equal to [result] if not null.
     */
    fun multiply(lhs: Vector, rhs: Int, result: Vector? = null): Vector {
        val v = result ?: Vector(0.0, 0.0, 0.0, 0.0)
        var sum: Float
        forEachColumn { c ->
            sum = 0f
            forEachColumn { i ->
                sum += lhs[i].toFloat() * this[rhs, i, c]
            }
            v[c] = sum.toDouble()
        }
        return v
    }

    /**
     * Transpose the matrix at [matrix].
     */
    fun transpose(matrix: Int) {
        var tmp: Float
        forEachColumn { r ->
            forEachColumn { c ->
                // Do not swap the same coefficients twice and do not swap the pivots at all:
                if (r < c) {
                    tmp = this[matrix, r, c]
                    this[matrix, r, c] = this[matrix, c, r]
                    this[matrix, c, r] = tmp
                }
            }
        }
    }

    /**
     * Prints the matrix at [matrix].
     */
    @Suppress("unused")
    fun toString(matrix: Int): String {
        val sb = StringBuilder()
        sb.append("[ ")
        forEachColumn { r ->
            forEachColumn { c ->
                sb.append(decimalFormat.format(this[matrix, r, c]))
                if (c < 3) sb.append(", ")
            }
            if (r < 3) sb.append(" | ")
        }
        sb.append(" ]")
        return sb.toString()
    }

}
