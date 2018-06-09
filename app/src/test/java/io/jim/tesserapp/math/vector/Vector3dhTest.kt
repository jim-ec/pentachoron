package io.jim.tesserapp.math.vector

import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.matrix.projection3dMatrix
import junit.framework.Assert.assertEquals
import org.junit.Test

class Vector3dhTest {

    private val vector = Vector3dh(1.0, 2.0, 3.0)

    @Test
    fun threeDimensionalButFourColumns() {
        assertEquals(3, vector.dimension)
        assertEquals(4, vector.cols)
    }

    @Test
    fun negate() {
        vector.apply {
            negate()
            assertEquals(-1.0, x, 0.1)
            assertEquals(-2.0, y, 0.1)
            assertEquals(-3.0, z, 0.1)
        }
    }

    @Test
    fun multiply() {
        val projection = projection3dMatrix()
        val translation = Matrix(4).apply { translation(Vector3d(0.0, 0.0, 1.0)) }
        val matrix = Matrix(4).apply { multiplication(translation, projection) }

        val result = Vector3dh()

        result.apply {
            multiplication(vector, matrix)

            assertEquals(1.0 / -4.0, x, 0.1)
            assertEquals(2.0 / -4.0, y, 0.1)
            assertEquals(-1.0, z, 0.1)
        }
    }

}
