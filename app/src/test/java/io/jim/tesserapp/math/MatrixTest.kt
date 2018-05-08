package io.jim.tesserapp.math

import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.util.RandomAccessBuffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatrixTest {

    private val matrix = Matrix(4)
    private val vector = Matrix.vector(4)
    private val result = Matrix.vector(4)

    @Test
    fun initialization() {
        assertEquals(4, matrix.rows)
        assertEquals(4, matrix.cols)
        matrix.forEachComponent { row, col ->
            assertEquals(if (row == col) 1f else 0f, matrix[row, col], 0.1f)
        }
    }

    @Test
    fun constructVectorMatrix() {
        val vec = Matrix.vector(4)
        assertEquals(1, vec.rows)
        assertEquals(4, vec.cols)
    }

    @Test
    fun constructNonQuadraticMatrix() {
        val vec = Matrix(4, 7)
        assertEquals(4, vec.rows)
        assertEquals(7, vec.cols)
    }

    @Test(expected = MathException::class)
    fun invalidMatrixDimension() {
        Matrix(0, -3)
    }

    @Test
    fun setGet() {
        matrix[2, 0] = 4f
        assertEquals(4f, matrix[2, 0], 0.1f)
    }

    @Test
    fun writeIntoBuffer() {
        val buffer = RandomAccessBuffer(10, matrix.bufferElementSize)
        matrix.writeIntoBuffer(2, buffer)

        matrix.forEachComponent { row, col ->
            assertEquals(if (row == col) 1f else 0f, buffer[2, row * matrix.cols + col], 0.1f)
        }
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
        vector.load(2f, 3f, 4f, 5f)

        matrix.forEachComponent { row, col ->
            matrix[row, col] = (col + 3f) + (row * matrix.cols)
        }

        result.apply {
            multiplication(vector, matrix)
            assertEquals(146f, x, 0.1f)
            assertEquals(160f, y, 0.1f)
            assertEquals(174f, z, 0.1f)
            assertEquals(188f, q, 0.1f)
        }
    }

    @Test(expected = MathException::class)
    fun invalidMultiplicationLhs() {
        val invalidLhs = Matrix(1, 5)
        val invalidTarget = Matrix(1, 2)
        invalidTarget.multiplication(invalidLhs, matrix)
    }

    @Test(expected = MathException::class)
    fun invalidMultiplicationTarget() {
        val validLhs = Matrix(1, 4)
        val invalidTarget = Matrix(4, 2)
        invalidTarget.multiplication(validLhs, matrix)
    }

    @Test(expected = Matrix.IsNotQuadraticException::class)
    fun translationNotQuadratic() {
        Matrix(3, 4).translation(2f, 5f, 6f)
    }

    @Test(expected = Matrix.IncompatibleTransformDimension::class)
    fun translationIncompatibleDimension() {
        matrix.translation(2f, 5f)
    }

    @Test
    fun translation() {
        matrix.translation(2f, 3f, 4f)
        vector.load(listOf(5f, 6f, 7f, 1f))
        result.multiplication(vector, matrix)

        result.apply {
            assertEquals(7f, x, 0.1f)
            assertEquals(9f, y, 0.1f)
            assertEquals(11f, z, 0.1f)
            assertEquals(1f, q, 0.1f)
        }
    }

    @Test
    fun rotation() {
        matrix.rotation(1, 2, Pi / 2)
        vector.load(0f, 3f, 0f, 5f)
        result.multiplication(vector, matrix)
        result.apply {
            assertEquals(0f, x, 0.1f)
            assertEquals(0f, y, 0.1f)
            assertEquals(3f, z, 0.1f)
            assertEquals(5f, q, 0.1f)
        }
    }

    @Test
    fun scaleUniformly() {
        matrix.scale(2f)
        vector.load(listOf(5f, 6f, 7f, 1f))
        result.multiplication(vector, matrix)

        result.apply {
            assertEquals(10f, x, 0.1f)
            assertEquals(12f, y, 0.1f)
            assertEquals(14f, z, 0.1f)
            assertEquals(1f, q, 0.1f)
        }
    }

    @Test
    fun scaleByIndividualFactors() {
        matrix.scale(2f, 3f, 4f)
        vector.load(listOf(5f, 6f, 7f, 1f))
        result.multiplication(vector, matrix)

        result.apply {
            assertEquals(10f, x, 0.1f)
            assertEquals(18f, y, 0.1f)
            assertEquals(28f, z, 0.1f)
            assertEquals(1f, q, 0.1f)
        }
    }

    @Test
    fun perspective2D() {
        matrix.perspective2D(5f, 10f)

        result.apply {
            multiplication(Matrix.vector(2f, 3f, -10f, 1f), matrix)
            perspectiveDivide()
            assertEquals(1f, z, 0.1f)
            assertEquals(2f / 10f, x, 0.1f)
            assertEquals(3f / 10f, y, 0.1f)
        }

        result.apply {
            multiplication(Matrix.vector(2f, 3f, -5f, 1f), matrix)
            perspectiveDivide()
            assertEquals(0f, z, 0.1f)
            assertEquals(2f / 5f, x, 0.1f)
            assertEquals(3f / 5f, y, 0.1f)
        }

        result.apply {
            multiplication(Matrix.vector(2f, 3f, -7f, 1f), matrix)
            perspectiveDivide()
            assertTrue(0f < z && z < 1f)
            assertEquals(2f / 7f, x, 0.1f)
            assertEquals(3f / 7f, y, 0.1f)
        }

        result.apply {
            multiplication(Matrix.vector(2f, 3f, -2f, 1f), matrix)
            perspectiveDivide()
            assertTrue(z < 0f)
            assertEquals(2f / 2f, x, 0.1f)
            assertEquals(3f / 2f, y, 0.1f)
        }
    }

    @Test
    fun lookAt() {
        matrix.lookAt(
                Vector(2f, 2f, 2f, 1f),
                Vector(0f, 0f, 0f, 1f),
                Vector(0f, 1f, 0f, 0f)
        )

        // Check the all matrix axis are unit vectors:
        assertEquals(1f, matrix.toVector(0).length, 0.1f)
        assertEquals(1f, matrix.toVector(1).length, 0.1f)
        assertEquals(1f, matrix.toVector(2).length, 0.1f)

        // Check that all matrix axis are perpendicular to each other:
        assertEquals(0f, matrix.toVector(0) * matrix.toVector(1), 0.1f)
        assertEquals(0f, matrix.toVector(1) * matrix.toVector(2), 0.1f)
        assertEquals(0f, matrix.toVector(0) * matrix.toVector(2), 0.1f)

        result.apply {
            multiplication(lhs = Matrix.vector(0f, 0f, 0f, 1f), rhs = matrix)
            assertEquals(0f, x, 0.1f)
            assertEquals(0f, y, 0.1f)
            assertTrue(z < 0f)
        }
    }

    @Test
    fun transpose() {
        matrix.load(
                listOf(1f, 5f, 9f, 13f),
                listOf(2f, 6f, 10f, 14f),
                listOf(3f, 7f, 11f, 15f),
                listOf(4f, 8f, 12f, 16f))

        matrix.transpose()

        result.apply {
            multiplication(lhs = Matrix.vector(1f, 2f, 3f, 4f), rhs = matrix)
            assertEquals(90f, x, 0.1f)
            assertEquals(100f, y, 0.1f)
            assertEquals(110f, z, 0.1f)
            assertEquals(120f, q, 0.1f)
        }
    }
}

