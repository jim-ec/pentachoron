package io.jim.tesserapp.math

import org.junit.Test

import org.junit.Assert.*

class MatrixTest {

    private val matrix = Matrix(4, 2)
    private val vector = Matrix.vector(4)

    @Test
    fun initialization() {
        matrix.forEachComponent { row, col ->
            assertEquals(if (row == col) 1f else 0f, matrix[row, col], 0.1f)
        }
    }

    @Test
    fun constructQuadraticMatrix() {
        val quad = Matrix(4)
        assertEquals(4, quad.rows)
        assertEquals(4, quad.cols)
    }

    @Test
    fun constructVectorMatrix() {
        val vec = Matrix.vector(4)
        assertEquals(1, vec.rows)
        assertEquals(4, vec.cols)
    }

    @Test(expected = RuntimeException::class)
    fun invalidMatrixDimension() {
        Matrix(0, -3)
    }

    @Test
    fun setGet() {
        matrix[2, 0] = 4f
        assertEquals(4f, matrix[2, 0], 0.1f)
    }

    @Test
    fun identity() {

        // Write random stuff into the matrix:
        matrix.forEachComponent { row, col ->
            matrix[row, col] = row + 3f * col
        }

        // Reload identity and check for it:
        matrix.identity()
        matrix.forEachComponent { row, col ->
            assertEquals(if (row == col) 1f else 0f, matrix[row, col], 0.1f)
        }
    }

    @Test
    fun multiply() {
        val target = Matrix(1, 2)
        vector.apply {
            x = 2f
            y = 3f
            z = 4f
            q = 5f
        }

        matrix.forEachComponent { row, col ->
            matrix[row, col] = (col + 3f) + (row * matrix.cols)
        }

        vector.multiply(matrix, target)
        assertEquals(94f, target.x, 0.1f)
        assertEquals(108f, target.y, 0.1f)
    }

    @Test(expected = RuntimeException::class)
    fun invalidMultiplicationLhs() {
        val invalidLhs = Matrix(1, 5)
        val invalidTarget = Matrix(1, 2)
        invalidLhs.multiply(matrix, invalidTarget)
    }

    @Test(expected = RuntimeException::class)
    fun invalidMultiplicationTarget() {
        val validLhs = Matrix(1, 4)
        val invalidTarget = Matrix(4, 2)
        validLhs.multiply(matrix, invalidTarget)
    }
}
