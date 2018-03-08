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

        assertEquals(v[0], 1.0, 0.1)
        assertEquals(v[1], 2.0, 0.1)
        assertEquals(v[2], 5.0, 0.1)
    }

    @Test
    fun vector1D() {
        val v = Vector1D(1.0)
        assertEquals(v.x, 1.0, 0.01)
    }

    @Test
    fun vector2D() {
        val v = Vector2D(1.0, 2.0)
        assertEquals(v.x, 1.0, 0.01)
        assertEquals(v.y, 2.0, 0.01)
    }

    @Test
    fun vector3D() {
        val v = Vector3D(1.0, 2.0, 3.0)
        assertEquals(v.x, 1.0, 0.01)
        assertEquals(v.y, 2.0, 0.01)
        assertEquals(v.z, 3.0, 0.01)
    }

    @Test
    fun vector4D() {
        val v = Vector4D(1.0, 2.0, 3.0, 4.0)
        assertEquals(v.x, 1.0, 0.01)
        assertEquals(v.y, 2.0, 0.01)
        assertEquals(v.z, 3.0, 0.01)
        assertEquals(v.w, 4.0, 0.01)
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

        assertEquals(w[0], 4.0, 0.1)
        assertEquals(w[1], 3.0, 0.1)
        assertEquals(w[2], 9.0, 0.1)
    }

    @Test
    fun subtraction() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        val u = Vector(doubleArrayOf(1.0, 2.0, 5.0))
        val w = v - u

        assertTrue(v compatible w)

        assertEquals(w[0], 2.0, 0.1)
        assertEquals(w[1], -1.0, 0.1)
        assertEquals(w[2], -1.0, 0.1)
    }

    @Test
    fun scalarMultiplication() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        val u = Vector(doubleArrayOf(1.0, 2.0, 5.0))
        assertEquals(v * u, 25.0, 0.1)
    }

    @Test
    fun division() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        assertEquals(v / 2.0, Vector(doubleArrayOf(1.5, 0.5, 2.0)))
    }

    @Test
    fun scale() {
        val v = Vector(doubleArrayOf(3.0, 1.0, 4.0))
        assertEquals(v * 2.0, Vector(doubleArrayOf(6.0, 2.0, 8.0)))
    }

    @Test
    fun cross() {
        val v = Vector3D(3.0, 1.0, 4.0)
        val u = Vector3D(1.0, 2.0, 5.0)
        assertEquals(v cross u, Vector3D(-3.0, -11.0, 5.0))
    }

    @Test
    fun length() {
        val v = Vector3D(3.0, 1.0, 4.0)
        assertEquals(v.length, Math.sqrt(26.0), 0.01)
    }

    @Test
    fun normalized() {
        val v = Vector3D(3.0, 1.0, 4.0)
        assertEquals(v.normalized.length, 1.0, 0.1)
    }

    @Test
    fun orthographicProjection() {
        val v = Vector3D(3.0, 1.0, 4.0)
        assertEquals(v.orthographicProjection.size, 2)
        assertEquals(v.orthographicProjection.x, 3.0, 0.01)
        assertEquals(v.orthographicProjection.y, 1.0, 0.01)
    }

}
