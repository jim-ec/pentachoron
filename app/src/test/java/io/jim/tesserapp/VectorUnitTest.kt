package io.jim.tesserapp

import io.jim.tesserapp.math.Vector
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Vector unit test.
 */
class VectorUnitTest {

    @Test
    fun construction() {
        Vector(1.0, 2.0, 5.0, 4.0).apply {
            assertEquals(1.0, this[0], 0.1)
            assertEquals(2.0, this[1], 0.1)
            assertEquals(5.0, this[2], 0.1)
            assertEquals(4.0, this[3], 0.1)

            assertEquals(1.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
            assertEquals(4.0, w, 0.1)
        }
    }

    @Test
    fun addition() {
        val v = Vector(1.0, 2.0, 5.0, 0.0)
        val u = Vector(3.0, 1.0, 4.0, 1.0)

        val w: Vector = v + u

        assertEquals(4.0, w[0], 0.1)
        assertEquals(3.0, w[1], 0.1)
        assertEquals(9.0, w[2], 0.1)
        assertEquals(1.0, w[3], 0.1)
    }

    @Test
    fun subtraction() {
        val v = Vector(3.0, 1.0, 4.0, 1.0)
        val u = Vector(1.0, 2.0, 5.0, 2.0)
        val w: Vector = v - u

        assertEquals(2.0, w[0], 0.1)
        assertEquals(-1.0, w[1], 0.1)
        assertEquals(-1.0, w[2], 0.1)
        assertEquals(-1.0, w[3], 0.1)
    }

    @Test
    fun scalarMultiplication() {
        val v = Vector(3.0, 1.0, 4.0, 0.0)
        val u = Vector(1.0, 2.0, 5.0, 3.0)
        assertEquals(25.0, v * u, 0.1)
    }

    @Test
    fun division() {
        (Vector(3.0, 1.0, 4.0, 0.0) / 2.0).apply {
            assertEquals(1.5, x, 0.1)
            assertEquals(0.5, y, 0.1)
            assertEquals(2.0, z, 0.1)
            assertEquals(0.0, w, 0.1)
        }
    }

    @Test
    fun scale() {
        (Vector(3.0, 1.0, 4.0, 4.0) * 2.0).apply {
            assertEquals(6.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(8.0, z, 0.1)
            assertEquals(8.0, w, 0.1)
        }
    }

    @Test
    fun cross() {
        (Vector(3.0, 1.0, 4.0, 1.0) cross Vector(1.0, 2.0, 5.0, 1.0)).apply {
            assertEquals(-3.0, x, 0.1)
            assertEquals(-11.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
            assertEquals(0.0, w, 0.1)
        }
    }

    @Test
    fun length() {
        assertEquals(Math.sqrt(26.0), Vector(3.0, 1.0, 4.0, 0.0).length, 0.01)
    }

    @Test
    fun normalized() {
        assertEquals(1.0, Vector(3.0, 1.0, 4.0, 5.0).normalize().length, 0.1)
    }

}
