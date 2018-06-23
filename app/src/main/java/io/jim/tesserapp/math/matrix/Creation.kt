package io.jim.tesserapp.math.matrix

/**
 * Create a square matrix, while initializing every coefficient.
 *
 * @param initializer
 * Called for each coefficient with the given row and column index.
 * The returned value represents the value for this specific matrix cell.
 */
fun quadratic(size: Int, initializer: (row: Int, col: Int) -> Double) = Matrix(size, size, initializer)

/**
 * Create an identity matrix.
 * @param size Side length of matrix.
 */
fun identity(size: Int) =
        quadratic(size) { row, col -> if (row == col) 1.0 else 0.0 }

/**
 * Create an identity matrix with a set of initial values associated to specific matrix cells.
 *
 * @param size
 * Side length of matrix.
 *
 * @param values
 * Initial values associated to a pair of row and column index.
 * Cells which are not explicitly initialized are set to match an identity matrix.
 */
fun identity(size: Int, values: Map<Pair<Int, Int>, Double>) =
        quadratic(size) { row, col ->
            values[row to col] ?: if (row == col) 1.0 else 0.0
        }
