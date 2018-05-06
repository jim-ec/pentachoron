package io.jim.tesserapp.math

import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.sin

/**
 * A bulk buffer storing 4x4 matrices as raw floats.
 */
class MatrixBuffer(

        /**
         * Maximum count of matrices this buffer can store.
         */
        val maxMatrices: Int

) {

    /**
     * Provides access to a matrix buffer, while the starting point and accessible range are both
     * well defined.
     *
     * Matrix indices in conjunction with such a memory space object are relative to the range's
     * offset into the buffer.
     */
    inner class MemorySpace(

            /**
             * The matrix at which this memory space begins.
             */
            val offset: Int,

            /**
             * The count of matrices this memory space spans over.
             */
            private val range: Int
    ) {

        constructor() : this(0, maxMatrices)

        /**
         * Thrown when invalid memory space is created.
         */
        inner class InvalidMemorySpaceException(msg: String)
            : MathException("Invalid memory space (at $offset, ranging over $range matrices): $msg")

        /**
         * Thrown when invalid index was specified.
         */
        inner class InvalidIndexException(msg: String)
            : MathException("Invalid index in memory space (at $offset, ranging over $range matrices): $msg")

        init {
            if (offset < 0 || offset + range > maxMatrices)
                throw InvalidMemorySpaceException("Memory range out of bounds")
            if (range < 0)
                throw InvalidMemorySpaceException("Range must be positive")
        }

        private fun checkMatrixIndex(matrix: Int) {
            if (matrix < 0)
                throw InvalidIndexException("Index must be positive")
            if (matrix >= range)
                throw InvalidIndexException("Index out of range")
        }

        /**
         * Set a float from a matrix to [value]. Index [matrix] is in virtual memory space.
         */
        operator fun set(matrix: Int, row: Int, column: Int, value: Float) {
            checkMatrixIndex(matrix)
            array[(offset + matrix) * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + column] = value
        }

        /**
         * Set the vector at [matrix], in [row] to [value].
         */
        operator fun set(matrix: Int, row: Int, value: Vector) {
            checkMatrixIndex(matrix)
            for (i in 0 until MATRIX_COLUMNS) {
                array[(offset + matrix) * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + i] = value[i]
            }
        }

        /**
         * Return a float from a matrix. Index [matrix] is in virtual memory space.
         */
        operator fun get(matrix: Int, row: Int, column: Int): Float {
            checkMatrixIndex(matrix)
            return array[(offset + matrix) * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + column]
        }

        /**
         * Return the vector at [matrix] and [row].
         * Allocates a new Vector object.
         */
        operator fun get(matrix: Int, row: Int): Vector {
            checkMatrixIndex(matrix)
            return Vector(
                    array[(offset + matrix) * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 0],
                    array[(offset + matrix) * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 1],
                    array[(offset + matrix) * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 2],
                    array[(offset + matrix) * MATRIX_COMPONENTS + row * MATRIX_COLUMNS + 3]
            )
        }

        private inline fun forEachColumn(f: (row: Int) -> Unit) {
            for (i in 0 until MATRIX_COLUMNS) f(i)
        }

        /**
         * Load the identity into [matrix].
         */
        fun identity(matrix: Int) {
            forEachColumn { row ->
                forEachColumn { col ->
                    this[matrix, row, col] = if (row == col) 1f else 0f
                }
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
                this[matrix, i, i] = v[i]
            }
        }

        /**
         * Load a rotation matrix into [matrix].
         * The rotation takes place on the given [a]-[b]-plane, the angle is defined in [radians].
         */
        fun rotation(matrix: Int, a: Int, b: Int, radians: Float) {
            this[matrix, a, a] = cos(radians)
            this[matrix, a, b] = sin(radians)
            this[matrix, b, a] = -sin(radians)
            this[matrix, b, b] = cos(radians)
        }

        /**
         * Load a translation matrix into [matrix].
         */
        fun translation(matrix: Int, v: Vector) {
            this[matrix, BASE_ROW] = v
        }

        /**
         * Load a 3D to 2D perspective matrix into [matrix].
         * The z component gets remapping between a near and far value.
         * @param near Near plane. If Vector lies on that plane (negated), it will be projected to 0.
         * @param far Far plane. If Vector lies on that plane (negated), it will be projected to 1.
         */
        fun perspective2D(matrix: Int, near: Float, far: Float) {
            if (near <= 0.0 || far <= 0.0 || near > far)
                throw MathException("Invalid near=$near or far=$far parameter")
            this[matrix, 2, 3] = -1.0f
            this[matrix, 3, 3] = 0.0f
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
        fun lookAt(matrix: Int, eye: Vector, target: Vector, refUp: Vector) = apply {

            val forward = (eye - target).normalize()
            val right = (refUp cross forward).normalize()

            this[matrix, FORWARD_ROW] = forward
            this[matrix, RIGHT_ROW] = right
            this[matrix, UP_ROW] = (forward cross right).normalize()
            this[matrix, BASE_ROW, 0] = 0f
            this[matrix, BASE_ROW, 1] = 0f
            this[matrix, BASE_ROW, 2] = 0f
            this[matrix, BASE_ROW, 3] = 1f

            transpose(matrix)

            this[matrix, BASE_ROW] = multiply(Vector(-eye.x, -eye.y, -eye.z, eye.w), matrix)
        }

        /**
         * Multiply the two matrices [lhs] and [rhs] into [matrix].
         */
        fun multiply(lhs: Int = 0, rhs: Int = 0, matrix: Int = 0) {
            multiply(lhs, this, rhs, this, matrix)
        }

        /**
         * Cross-buffer multiplication.
         *
         * Multiplies [lhs], located in [lhsMemorySpace], and [rhs], located in [rhsMemorySpace],
         * into [matrix].
         */
        fun multiply(
                lhs: Int = 0, lhsMemorySpace: MemorySpace,
                rhs: Int = 0, rhsMemorySpace: MemorySpace,
                matrix: Int = 0) {
            var sum: Float
            forEachColumn { r ->
                forEachColumn { c ->
                    sum = 0f
                    forEachColumn { i ->
                        sum += lhsMemorySpace[lhs, r, i] * rhsMemorySpace[rhs, i, c]
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
        fun multiply(lhs: Vector, rhs: Int = 0, result: Vector? = null): Vector {
            val v = result ?: Vector(0f, 0f, 0f, 0f)
            var sum: Float
            forEachColumn { c ->
                sum = 0f
                forEachColumn { i ->
                    sum += lhs[i] * this[rhs, i, c]
                }
                v[c] = sum
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
         * Copy the matrix at [source] to matrix at [destination].
         */
        fun copy(destination: Int = 0, source: Int = 0) {
            copy(destination, source, this)
        }

        /**
         * Copy the matrix at [source] inside [sourceMemorySpace] to matrix at [destination].
         */
        fun copy(destination: Int = 0, source: Int = 0, sourceMemorySpace: MemorySpace) {
            forEachColumn { r ->
                forEachColumn { c ->
                    this[destination, r, c] = sourceMemorySpace[source, r, c]
                }
            }
        }

        override fun toString() =
                "Memory at $offset spanning over $range ${if (range > 1) "buffer" else "matrix"}:\n"

        /**
         * Prints the whole matrix buffer into a string.
         */
        @Suppress("unused")
        fun joinToString(indent: Int): String {
            val sb = StringBuilder()
            val digits = log10(range.toDouble()).toInt() + 1
            for (matrix in 0 until range) {
                for (ind in 0 until 4 * indent) sb.append(' ')
                sb.append("#%${digits}d: [ ".format(matrix))
                forEachColumn { r ->
                    forEachColumn { c ->
                        sb.append(decimalFormat.format(this[matrix, r, c]))
                        if (c < 3) sb.append(", ")
                    }
                    if (r < 3) sb.append(" | ")
                }
                sb.append(" ]\n")
            }
            return sb.toString()
        }
    }

    /**
     * The underlying float array.
     */
    val array = FloatArray(maxMatrices * MATRIX_COMPONENTS)

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

    init {
        // Initialize all matrices to identity matrices:
        val memory = MemorySpace()
        for (i in 0 until maxMatrices) {
            memory.identity(i)
        }
    }

}
