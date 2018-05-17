package io.jim.tesserapp.math.transform

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.common.Pi
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.math.vector.Vector3dh
import io.jim.tesserapp.math.vector.VectorN
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MatrixTest {

    private val matrix = Matrix(4)

    @Test
    fun initialization() {
        assertEquals(4, matrix.rows)
        assertEquals(4, matrix.cols)
        matrix.forEachComponent { row, col ->
            assertEquals(if (row == col) 1f else 0f, matrix[row, col], 0.1f)
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
        Matrix(3, 4).translation(Vector3d(2f, 5f, 6f))
    }

    @Test(expected = Matrix.IncompatibleTransformDimension::class)
    fun translationIncompatibleDimension() {
        matrix.translation(VectorN(2f, 5f))
    }

    @Test
    fun translation() {
        matrix.translation(VectorN(2f, 3f, 4f))
        val vector = Vector3dh(5f, 6f, 7f)

        Vector3dh().apply {
            multiplication(vector, matrix)

            assertEquals(7f, x, 0.1f)
            assertEquals(9f, y, 0.1f)
            assertEquals(11f, z, 0.1f)
        }
    }

    @Test
    fun rotation() {
        matrix.rotation(1, 2, Pi / 2)
        val vector = Vector3dh(0f, 3f, 0f)

        Vector3dh().apply {
            multiplication(vector, matrix)

            assertEquals(0f, x, 0.1f)
            assertEquals(0f, y, 0.1f)
            assertEquals(3f, z, 0.1f)
        }
    }

    @Test
    fun scaleUniformly() {
        matrix.scale(2f)
        val vector = Vector3dh(5f, 6f, 7f)

        Vector3dh().apply {
            multiplication(vector, matrix)

            assertEquals(10f, x, 0.1f)
            assertEquals(12f, y, 0.1f)
            assertEquals(14f, z, 0.1f)
        }
    }

    @Test
    fun scaleByIndividualFactors() {
        matrix.scale(VectorN(2f, 3f, 4f))
        val vector = Vector3dh(5f, 6f, 7f)

        Vector3dh().apply {
            multiplication(vector, matrix)

            assertEquals(10f, x, 0.1f)
            assertEquals(18f, y, 0.1f)
            assertEquals(28f, z, 0.1f)
        }
    }

    @Test
    fun perspective2D() {
        matrix.perspective2D(5f, 10f)

        val homogeneous = Vector3dh()

        homogeneous.apply {
            multiplication(Vector3dh(2f, 3f, -10f), matrix)
            assertEquals(1f, z, 0.1f)
            assertEquals(2f / 10f, x, 0.1f)
            assertEquals(3f / 10f, y, 0.1f)
        }

        homogeneous.apply {
            multiplication(Vector3dh(2f, 3f, -5f), matrix)
            assertEquals(0f, z, 0.1f)
            assertEquals(2f / 5f, x, 0.1f)
            assertEquals(3f / 5f, y, 0.1f)
        }

        homogeneous.apply {
            multiplication(Vector3dh(2f, 3f, -7f), matrix)
            assertTrue(0f < z && z < 1f)
            assertEquals(2f / 7f, x, 0.1f)
            assertEquals(3f / 7f, y, 0.1f)
        }

        homogeneous.apply {
            multiplication(Vector3dh(2f, 3f, -2f), matrix)
            assertTrue(z < 0f)
            assertEquals(2f / 2f, x, 0.1f)
            assertEquals(3f / 2f, y, 0.1f)
        }
    }

    @Test
    fun lookAt() {
        val matrix = LookAtMatrix()

        matrix.lookAt(
                eye = Vector3d(2f, 2f, 2f),
                target = Vector3d(0f, 0f, 0f),
                refUp = Vector3d(0f, 1f, 0f)
        )

        // Check the all matrix axis are unit vectors:
        assertEquals(1f, matrix.toVector(0).length, 0.1f)
        assertEquals(1f, matrix.toVector(1).length, 0.1f)
        assertEquals(1f, matrix.toVector(2).length, 0.1f)

        // Check that all matrix axis are perpendicular to each other:
        assertEquals(0f, matrix.toVector(0) * matrix.toVector(1), 0.1f)
        assertEquals(0f, matrix.toVector(1) * matrix.toVector(2), 0.1f)
        assertEquals(0f, matrix.toVector(0) * matrix.toVector(2), 0.1f)

        Vector3dh().apply {
            multiplication(lhs = Vector3dh(0f, 0f, 0f), rhs = matrix)
            assertEquals(0f, x, 0.1f)
            assertEquals(0f, y, 0.1f)
            assertTrue(z < 0f)
        }
    }

    @Test
    fun transpose() {
        matrix.load(
                VectorN(1f, 5f, 9f, 13f),
                VectorN(2f, 6f, 10f, 14f),
                VectorN(3f, 7f, 11f, 15f),
                VectorN(4f, 8f, 12f, 16f))

        matrix.transpose()

        Vector3dh().apply {
            multiplication(lhs = Vector3dh(1f, 2f, 3f), rhs = matrix)
            val w = 72f
            assertEquals(52f / w, x, 0.1f)
            assertEquals(58f / w, y, 0.1f)
            assertEquals(65f / w, z, 0.1f)
        }
    }
}

