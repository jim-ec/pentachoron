package io.jim.tesserapp.math.matrix

/**
 * Create an identity matrix.
 * @param size Side length of matrix.
 */
fun identity(size: Int) =
        Matrix(size, size) { row, col -> if (row == col) 1.0 else 0.0 }
