package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.vector.VectorN
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatrixTest {

    /**
     * Converts a row into a [VectorN].
     * The [VectorN.dimension] is determined by this matrix' column count.
     */
    fun Matrix.toVector(row: Int) = VectorN(cols).also {
        for (i in 0 until it.dimension) {
            it[i] = this[row, i]
        }
    }

    private val matrix = Matrix(4)

    @Test
    fun initialization() {
        assertEquals(4, matrix.rows)
        assertEquals(4, matrix.cols)
        matrix.forEachComponent { row, col ->
            assertEquals(if (row == col) 1.0 else 0.0, matrix[row, col], 0.1)
        }
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
        matrix[2, 0] = 4.0
        assertEquals(4.0, matrix[2, 0], 0.1)
    }

    @Test
    fun identity() {

        // Write random stuff into the matrix:
        matrix.forEachComponent { row, col ->
            matrix[row, col] = row + 3.0 * col
        }

        // Reload identity and check for it:
        matrix.identity()
        matrix.forEachComponent { row, col ->
            assertEquals(if (row == col) 1.0 else 0.0, matrix[row, col], 0.1)
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
        Matrix(3, 4).translation(VectorN(2.0, 5.0, 6.0))
    }

    @Test(expected = Matrix.IncompatibleTransformDimension::class)
    fun translationIncompatibleDimension() {
        matrix.translation(VectorN(2.0, 5.0, 1.0, 1.0))
    }

    @Test
    fun translation() {
        matrix.translation(VectorN(2.0, 3.0, 4.0))
        val vector = VectorN(5.0, 6.0, 7.0)
    
        (vector * matrix).apply {
            assertEquals(7.0, x, 0.1)
            assertEquals(9.0, y, 0.1)
            assertEquals(11.0, z, 0.1)
        }
    }

    @Test
    fun rotation() {
        matrix.rotation(1, 2, Math.PI / 2)
        val vector = VectorN(0.0, 3.0, 0.0)
    
        (vector * matrix).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(3.0, z, 0.1)
        }
    }

    @Test
    fun scaleUniformly() {
        matrix.scale(2.0)
        val vector = VectorN(5.0, 6.0, 7.0)
    
        (vector * matrix).apply {
            assertEquals(10.0, x, 0.1)
            assertEquals(12.0, y, 0.1)
            assertEquals(14.0, z, 0.1)
        }
    }

    @Test
    fun scaleByIndividualFactors() {
        matrix.scale(VectorN(2.0, 3.0, 4.0))
        val vector = VectorN(5.0, 6.0, 7.0)
    
        (vector * matrix).apply {
            assertEquals(10.0, x, 0.1)
            assertEquals(18.0, y, 0.1)
            assertEquals(28.0, z, 0.1)
        }
    }

    @Test
    fun perspective2D() {
        matrix.perspective2D(5.0, 10.0)
    
        (VectorN(2.0, 3.0, -10.0) * matrix).apply {
            assertEquals(1.0, z, 0.1)
            assertEquals(2.0 / 10.0, x, 0.1)
            assertEquals(3.0 / 10.0, y, 0.1)
        }
    
        (VectorN(2.0, 3.0, -5.0) * matrix).apply {
            assertEquals(0.0, z, 0.1)
            assertEquals(2.0 / 5.0, x, 0.1)
            assertEquals(3.0 / 5.0, y, 0.1)
        }
    
        (VectorN(2.0, 3.0, -7.0) * matrix).apply {
            assertTrue(0.0 < z && z < 1.0)
            assertEquals(2.0 / 7.0, x, 0.1)
            assertEquals(3.0 / 7.0, y, 0.1)
        }
    
        (VectorN(2.0, 3.0, -2.0) * matrix).apply {
            assertTrue(z < 0.0)
            assertEquals(2.0 / 2.0, x, 0.1)
            assertEquals(3.0 / 2.0, y, 0.1)
        }
    }

    @Test
    fun lookAt() {
        val matrix = LookAtMatrix().computed(
                distance = 2.0,
                refUp = VectorN(0.0, 1.0, 0.0)
        )

        // Check the all matrix axis are unit vectors:
        assertEquals(1.0, matrix.toVector(0).length, 0.1)
        assertEquals(1.0, matrix.toVector(1).length, 0.1)
        assertEquals(1.0, matrix.toVector(2).length, 0.1)

        // Check that all matrix axis are perpendicular to each other:
        assertEquals(0.0, matrix.toVector(0) * matrix.toVector(1), 0.1)
        assertEquals(0.0, matrix.toVector(1) * matrix.toVector(2), 0.1)
        assertEquals(0.0, matrix.toVector(0) * matrix.toVector(2), 0.1)
    
        (VectorN(0.0, 0.0, 0.0) * matrix).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertTrue(z < 0.0)
        }
    }

    @Test
    fun transpose() {
        matrix.load(
                VectorN(1.0, 5.0, 9.0, 13.0),
                VectorN(2.0, 6.0, 10.0, 14.0),
                VectorN(3.0, 7.0, 11.0, 15.0),
                VectorN(4.0, 8.0, 12.0, 16.0))

        matrix.transpose()
    
        (VectorN(1.0, 2.0, 3.0) * matrix).apply {
            val w = 72.0
            assertEquals(52.0 / w, x, 0.1)
            assertEquals(58.0 / w, y, 0.1)
            assertEquals(65.0 / w, z, 0.1)
        }
    }
}

