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
        Matrix(4).apply {
            assertEquals(4, rows)
            assertEquals(4, cols)
        
            forEachComponent { row, col ->
                assertEquals(if (row == col) 1.0 else 0.0, this[row, col], 0.1)
            }
        }
    }
    
    @Test
    fun constructNonQuadraticMatrix() {
        Matrix(4, 7).apply {
            assertEquals(4, rows)
            assertEquals(7, cols)
        }
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
    
    @Test(expected = MathException::class)
    fun invalidMultiplicationLhs() {
        Matrix(1, 5) * matrix
    }
    
    @Test(expected = MathException::class)
    fun translationIncompatibleDimension() {
        Matrix.translation(4, VectorN(2.0, 5.0, 1.0, 1.0))
    }
    
    @Test
    fun translation() {
        (VectorN(5.0, 6.0, 7.0) * Matrix.translation(4, VectorN(2.0, 3.0, 4.0))).apply {
            assertEquals(7.0, x, 0.1)
            assertEquals(9.0, y, 0.1)
            assertEquals(11.0, z, 0.1)
        }
    }
    
    @Test
    fun rotation() {
        (VectorN(0.0, 3.0, 0.0) * Matrix.rotation(4, 1, 2, Math.PI / 2)).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(3.0, z, 0.1)
        }
    }
    
    @Test
    fun scaleByIndividualFactors() {
        (VectorN(5.0, 6.0, 7.0) * Matrix.scale(4, VectorN(2.0, 3.0, 4.0))).apply {
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
        val matrix = lookAt(
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
        val matrix = Matrix(4, 4) { row, col -> row + col * 4.0 }
        
        (VectorN(1.0, 2.0, 3.0) * matrix.transposed()).apply {
            val w = 72.0
            assertEquals(52.0 / w, x, 0.1)
            assertEquals(58.0 / w, y, 0.1)
            assertEquals(65.0 / w, z, 0.1)
        }
    }
}

