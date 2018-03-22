package io.jim.tesserapp

import io.jim.tesserapp.math.Vector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Vector unit test.
 */
class VectorUnitTest {

    @Test
    fun construction() {
        val v = Vector(1.0, 2.0, 5.0, 4.0)

        assertEquals(v.size, 4)

        assertEquals(1.0, v[0], 0.1)
        assertEquals(2.0, v[1], 0.1)
        assertEquals(5.0, v[2], 0.1)
        assertEquals(4.0, v[3], 0.01)

        assertEquals(1.0, v.x, 0.01)
        assertEquals(2.0, v.y, 0.01)
        assertEquals(5.0, v.z, 0.01)
        assertEquals(4.0, v.w, 0.01)
    }

    @Test
    fun nullVector() {
        val v = Vector(0.0, 0.0, 0.0, -0.0, -0.0)
        assertTrue(v.isNull)
    }

    @Test
    fun compatibility() {
        val v = Vector(1.0, 2.0, 5.0)
        val u = Vector(3.0, 1.0, 4.0)

        assertTrue(v compatible u)
    }

    @Test
    fun addition() {
        val v = Vector(1.0, 2.0, 5.0)
        val u = Vector(3.0, 1.0, 4.0)

        val w = v + u

        assertTrue(v compatible w)

        assertEquals(4.0, w[0], 0.1)
        assertEquals(3.0, w[1], 0.1)
        assertEquals(9.0, w[2], 0.1)
    }

    @Test
    fun subtraction() {
        val v = Vector(3.0, 1.0, 4.0)
        val u = Vector(1.0, 2.0, 5.0)
        val w = v - u

        assertTrue(v compatible w)

        assertEquals(2.0, w[0], 0.1)
        assertEquals(-1.0, w[1], 0.1)
        assertEquals(-1.0, w[2], 0.1)
    }

    @Test
    fun scalarMultiplication() {
        val v = Vector(3.0, 1.0, 4.0)
        val u = Vector(1.0, 2.0, 5.0)
        assertEquals(25.0, v * u, 0.1)
    }

    @Test
    fun division() {
        val v = Vector(3.0, 1.0, 4.0)
        assertEquals(Vector(1.5, 0.5, 2.0), v / 2.0)
    }

    @Test
    fun scale() {
        val v = Vector(3.0, 1.0, 4.0)
        assertEquals(Vector(6.0, 2.0, 8.0), v * 2.0)
    }

    @Test
    fun cross() {
        val v = Vector(3.0, 1.0, 4.0)
        val u = Vector(1.0, 2.0, 5.0)
        assertEquals(Vector(-3.0, -11.0, 5.0), v cross u)
    }

    @Test
    fun length() {
        val v = Vector(3.0, 1.0, 4.0)
        assertEquals(Math.sqrt(26.0), v.length, 0.01)
    }

    @Test
    fun normalized() {
        val v = Vector(3.0, 1.0, 4.0)
        assertEquals(1.0, v.normalized.length, 0.1)
    }

    @Test
    fun orthographicProjection() {
        val v = Vector(3.0, 1.0, 4.0)
        v.orthographicProjection.apply {
            assertEquals(2, size)
            assertEquals(3.0, x, 0.01)
            assertEquals(1.0, y, 0.01)
        }
    }

    @Test
    fun perspectiveProjection() {
        val v = Vector(3.0, 5.0, 9.0)
        v.perspectiveProjection.apply {
            assertEquals(2, size)
            assertEquals(0.33, x, 0.01)
            assertEquals(0.55, y, 0.01)
        }
    }

}
