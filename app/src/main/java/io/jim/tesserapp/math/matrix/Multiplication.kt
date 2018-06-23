package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.common.MathException

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
 * @throws MathException If the dimension requirement `MxP * PxN = MxN` is not met.
 */
operator fun Matrix.times(rhs: Matrix) =
        if (cols != rhs.rows)
            throw MathException("Cannot multiply $this * $rhs")
        else
            Matrix(rows, rhs.cols) { row, col ->
                (0 until cols).sumByDouble { this[row, it] * rhs[it, col] }
            }
