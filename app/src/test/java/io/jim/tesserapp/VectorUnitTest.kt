package io.jim.tesserapp

import io.jim.tesserapp.math.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Vector unit test.
 */
class VectorUnitTest {

    @Test
    fun construction() {
        val v = Vector(doubleArrayOf(1.0, 2.0, 5.0))

        assertEquals(v.size, 3)

        assertEquals(1.0, v[0], 0.1)
        assertEquals(2.0, v[1], 0.1)
        assertEquals(5.0, v[2], 0.1)
    }

    @Test
    fun vector1D() {
        val v = Vector1D(1.0)
        assertEquals(1.0, v.x, 0.01)
    }

    @Test
    fun vector2D() {
        val v = Vector2D(1.0, 2.0)
        assertEquals(1.0, v.x, 0.01)
        assertEquals(2.0, v.y, 0.01)
    }

    @Test
    fun vector3D() {
        val v = Vector3D(1.0, 2.0, 3.0)
        assertEquals(1.0, v.x, 0.01)
        assertEquals(2.0, v.y, 0.01)
        assertEquals(3.0, v.z, 0.01)
    }

    @Test
    fun vector4D() {
        val v = Vector4D(1.0, 2.0, 3.0, 4.0)
        assertEquals(1.0, v.x, 0.01)
        assertEquals(2.0, v.y, 0.01)
        assertEquals(3.0, v.z, 0.01)
        assertEquals(4.0, v.w, 0.01)
    }

    @Test
    fun nullVector() {
        val v = Vector(doubleArrayOf(0.0, 0.0, 0.0, -0.0, -0.0))
        assertTrue(v.isNull)
    }

    @Test
    fun compatibility() {
        val v = Vector(doubleArrayOf(1.0, 2.0, 5.0))
        val u = Vector(doubleArrayOf(3.0, 1.0, 4.0))

        assertTrue(v compatible u)
    }

    @Test
    fun addition() {
        val v = Vector(doubleArrayOf(1.0, 2.0, 5.0))
        val u = Vector(doubleArrayOf(3.0, 1.0, 4.0))

        val w = v + u

        assertTrue(v compatible w)

        assertEquals(4.0, w[0], 0.1)
        assertEquals(3.0, w[1], 0.1)
        assertEquals(9.0, w[2], 0.1)
    }

    @Test
    fun subtraction() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        val u = Vector(doubleArrayOf(1.0, 2.0, 5.0))
        val w = v - u

        assertTrue(v compatible w)

        assertEquals(2.0, w[0], 0.1)
        assertEquals(-1.0, w[1], 0.1)
        assertEquals(-1.0, w[2], 0.1)
    }

    @Test
    fun scalarMultiplication() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        val u = Vector(doubleArrayOf(1.0, 2.0, 5.0))
        assertEquals(25.0, v * u, 0.1)
    }

    @Test
    fun division() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        assertEquals(Vector(doubleArrayOf(1.5, 0.5, 2.0)), v / 2.0)
    }

    @Test
    fun scale() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        assertEquals(Vector(doubleArrayOf(6.0, 2.0, 8.0)), v * 2.0)
    }

    @Test
    fun cross() {
        val v = Vector3D(3.0, 1.0, 4.0)
        val u = Vector3D(1.0, 2.0, 5.0)
        assertEquals(Vector3D(-3.0, -11.0, 5.0), v cross u)
    }

    @Test
    fun length() {
        val v = Vector3D(3.0, 1.0, 4.0)
        assertEquals(Math.sqrt(26.0), v.length, 0.01)
    }

    @Test
    fun normalized() {
        val v = Vector3D(3.0, 1.0, 4.0)
        assertEquals(1.0, v.normalized.length, 0.1)
    }

    @Test
    fun orthographicProjection() {
        val v = Vector3D(3.0, 1.0, 4.0)
        v.orthographicProjection.apply {
            assertEquals(2, size)
            assertEquals(3.0, x, 0.01)
            assertEquals(1.0, y, 0.01)
        }
    }

    @Test
    fun perspectiveProjection() {
        val v = Vector3D(3.0, 5.0, 9.0)

        (v perspectiveProjection 2.5).apply {
            assertTrue(this is Vector2D)
            this as Vector2D

            assertEquals(2, size)
            assertEquals(0.83, x, 0.01)
            assertEquals(1.38, y, 0.01)
        }
    }

}
