package io.jim.tesserapp.math

import io.jim.tesserapp.util.assertEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

/**
 * Vector unit test.
 */
class VectorTest {

    @Test
    fun construction() {
        Vector(1f, 2f, 5f, 4f).apply {
            assertEquals(1f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
            assertEquals(4f, q, 0.1f)

            assertEquals(1f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
            assertEquals(4f, q, 0.1f)
        }
    }

    @Test
    fun addition() {
        val v = Vector(1f, 2f, 5f, 0f)
        val u = Vector(3f, 1f, 4f, 1f)

        val w: Vector = v + u

        assertEquals(4f, w.x, 0.1f)
        assertEquals(3f, w.y, 0.1f)
        assertEquals(9f, w.z, 0.1f)
        assertEquals(1f, w.q, 0.1f)
    }

    @Test
    fun subtraction() {
        val v = Vector(3f, 1f, 4f, 1f)
        val u = Vector(1f, 2f, 5f, 2f)
        val w: Vector = v - u

        assertEquals(2f, w.x, 0.1f)
        assertEquals(-1f, w.y, 0.1f)
        assertEquals(-1f, w.z, 0.1f)
        assertEquals(-1f, w.q, 0.1f)
    }

    @Test
    fun scalarMultiplication() {
        val v = Vector(3f, 1f, 4f, 0f)
        val u = Vector(1f, 2f, 5f, 3f)
        assertEquals(25f, v * u, 0.1f)
    }

    @Test
    fun division() {
        (Vector(3f, 1f, 4f, 0f) / 2f).apply {
            assertEquals(1.5f, x, 0.1f)
            assertEquals(0.5f, y, 0.1f)
            assertEquals(2f, z, 0.1f)
            assertEquals(0f, q, 0.1f)
        }
    }

    @Test
    fun scale() {
        (Vector(3f, 1f, 4f, 4f) * 2f).apply {
            assertEquals(6f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(8f, z, 0.1f)
            assertEquals(8f, q, 0.1f)
        }
    }

    @Test
    fun cross() {
        (Vector(3f, 1f, 4f, 1f) cross Vector(1f, 2f, 5f, 1f)).apply {
            assertEquals(-3f, x, 0.1f)
            assertEquals(-11f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
            assertEquals(0f, q, 0.1f)
        }
    }

    @Test
    fun length() {
        assertEquals(sqrt(26f), Vector(3f, 1f, 4f, 0f).length, 0.1f)
    }

    @Test
    fun normalized() {
        assertEquals(1f, Vector(3f, 1f, 4f, 5f).normalize().length, 0.1f)
    }

    @Test
    fun perspectiveDivision() {
        assertEquals(
                Vector(6f, 2f, 4f, 2f),
                Vector(3f, 1f, 2f, 1f).apply { perspectiveDivide() },
                0.1f
        )
    }

}
