package io.jim.tesserapp.math.vector

import io.jim.tesserapp.math.transform.Matrix
import io.jim.tesserapp.math.transform.Projection3dMatrix
import junit.framework.Assert.assertEquals
import org.junit.Test

class Vector3dhTest {

    private val vector = Vector3dh(1f, 2f, 3f)

    @Test
    fun threeDimensionalButFourColumns() {
        assertEquals(3, vector.dimension)
        assertEquals(4, vector.cols)
    }

    @Test
    fun negate() {
        vector.apply {
            negate()
            assertEquals(-1f, x, 0.1f)
            assertEquals(-2f, y, 0.1f)
            assertEquals(-3f, z, 0.1f)
        }
    }

    @Test
    fun multiply() {
        val projection = Projection3dMatrix()
        val translation = Matrix(4).apply { translation(Vector3d(0f, 0f, 1f)) }
        val matrix = Matrix(4).apply { multiplication(translation, projection) }

        val result = Vector3dh()

        result.apply {
            multiplication(vector, matrix)

            assertEquals(1f / -4f, x, 0.1f)
            assertEquals(2f / -4f, y, 0.1f)
            assertEquals(-1f, z, 0.1f)
        }
    }

}
