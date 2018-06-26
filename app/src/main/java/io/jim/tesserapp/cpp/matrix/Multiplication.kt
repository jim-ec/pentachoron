package io.jim.tesserapp.cpp.matrix

/**
 * Return a transform matrix computed by accumulating all the [matrices].
 * The first matrix is always local-aligned, the last one is always global-aligned.
 *
 * This function is intended to be preferred over [Matrix.times] when many matrices are multiplied together.
 */
fun transformChain(matrices: List<Matrix>) = matrices.reduce { acc, matrix -> acc * matrix }

/**
 * Multiply this and [rhs] matrix returning the resulting matrix.
 *
 * @throws RuntimeException If the dimension requirement `MxP * PxN = MxN` is not met.
 */
operator fun Matrix.times(rhs: Matrix) =
        if (cols != rhs.rows)
            throw RuntimeException("Cannot multiply matrices")
        else
            Matrix(rows, rhs.cols) { row, col ->
                (0 until cols).sumByDouble { this[row, it] * rhs[it, col] }
            }
