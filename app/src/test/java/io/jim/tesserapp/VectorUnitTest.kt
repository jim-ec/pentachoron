package io.jim.tesserapp

import io.jim.tesserapp.math.SphericalCoordinate
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
        Vector(1.0, 2.0, 5.0, 4.0).apply {

            assertEquals(4, dimension)

            assertEquals(1.0, this[0], 0.1)
            assertEquals(2.0, this[1], 0.1)
            assertEquals(5.0, this[2], 0.1)
            assertEquals(4.0, this[3], 0.1)

            assertEquals(1.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(5.0, z, 0.1)
            assertEquals(4.0, q, 0.1)
        }
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

        val w: Vector = v + u

        assertTrue(v compatible w)

        assertEquals(4.0, w[0], 0.1)
        assertEquals(3.0, w[1], 0.1)
        assertEquals(9.0, w[2], 0.1)
    }

    @Test
    fun subtraction() {
        val v = Vector(3.0, 1.0, 4.0)
        val u = Vector(1.0, 2.0, 5.0)
        val w: Vector = v - u

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
        assertEquals(Vector(1.5, 0.5, 2.0), Vector(3.0, 1.0, 4.0) / 2.0)
        assertEquals(Vector(1.5, 0.5, 2.0), Vector(3.0, 1.0, 4.0) / 2.0)
    }

    @Test
    fun scale() {
        assertEquals(Vector(6.0, 2.0, 8.0), Vector(3.0, 1.0, 4.0) * 2.0)
        assertEquals(Vector(6.0, 2.0, 8.0), Vector(3.0, 1.0, 4.0) * 2.0)
    }

    @Test
    fun negation() {
        (Vector(2.0, 1.0)).apply {
            -this
            assertEquals(-2.0, x, 0.1)
            assertEquals(-1.0, y, 0.1)
        }
        (Vector(2.0, 1.0)).apply {
            -this
            assertEquals(-2.0, x, 0.1)
            assertEquals(-1.0, y, 0.1)
        }
    }

    @Test
    fun cross() {
        assertEquals(Vector(-3.0, -11.0, 5.0), Vector(3.0, 1.0, 4.0) cross Vector(1.0, 2.0, 5.0))
    }

    @Test
    fun length() {
        assertEquals(Math.sqrt(26.0), Vector(3.0, 1.0, 4.0).length, 0.01)
        assertEquals(Math.sqrt(26.0), Vector(3.0, 1.0, 4.0).length, 0.01)
    }

    @Test
    fun normalized() {
        assertEquals(1.0, Vector(3.0, 1.0, 4.0).apply { normalize() }.length, 0.1)
        assertEquals(1.0, Vector(3.0, 1.0, 4.0).apply { normalize() }.length, 0.1)
    }

    @Test
    fun spherical() {
        val v = Vector(1.0, 4.0, 2.0)
        val p = SphericalCoordinate(v)
        assertEquals(4.58, p.r, 0.01)
        assertEquals(1.11, p.theta, 0.01)
        assertEquals(1.32, p.phi, 0.01)
        val u = Vector(p)
        assertEquals(u.x, v.x, 0.01)
        assertEquals(u.y, v.y, 0.01)
        assertEquals(u.z, v.z, 0.01)
    }

}
