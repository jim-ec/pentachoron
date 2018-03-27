package io.jim.tesserapp

import io.jim.tesserapp.math.Direction
import io.jim.tesserapp.math.Point
import io.jim.tesserapp.math.SphericalCoordinate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Vector unit test.
 */
class VectorUnitTest {

    @Test
    fun construction() {
        val v = Point(1.0, 2.0, 5.0, 4.0)

        assertEquals(v.dimension, 4)

        assertEquals(1.0, v[0], 0.1)
        assertEquals(2.0, v[1], 0.1)
        assertEquals(5.0, v[2], 0.1)
        assertEquals(4.0, v[3], 0.1)

        assertEquals(1.0, v.x, 0.1)
        assertEquals(2.0, v.y, 0.1)
        assertEquals(5.0, v.z, 0.1)
        assertEquals(4.0, v.w, 0.1)
    }

    @Test
    fun compatibility() {
        val v = Point(1.0, 2.0, 5.0)
        val u = Point(3.0, 1.0, 4.0)

        assertTrue(v compatible u)
    }

    @Test
    fun addition() {
        val v = Point(1.0, 2.0, 5.0)
        val u = Direction(3.0, 1.0, 4.0)

        val w: Point = v + u

        assertTrue(v compatible w)

        assertEquals(4.0, w[0], 0.1)
        assertEquals(3.0, w[1], 0.1)
        assertEquals(9.0, w[2], 0.1)
    }

    @Test
    fun subtraction() {
        val v = Point(3.0, 1.0, 4.0)
        val u = Point(1.0, 2.0, 5.0)
        val w: Direction = v - u

        assertTrue(v compatible w)

        assertEquals(2.0, w[0], 0.1)
        assertEquals(-1.0, w[1], 0.1)
        assertEquals(-1.0, w[2], 0.1)
    }

    @Test
    fun scalarMultiplication() {
        val v = Point(3.0, 1.0, 4.0)
        val u = Direction(1.0, 2.0, 5.0)
        assertEquals(25.0, v * u, 0.1)
    }

    @Test
    fun division() {
        assertEquals(Point(1.5, 0.5, 2.0), Point(3.0, 1.0, 4.0) / 2.0)
        assertEquals(Direction(1.5, 0.5, 2.0), Direction(3.0, 1.0, 4.0) / 2.0)
    }

    @Test
    fun scale() {
        assertEquals(Point(6.0, 2.0, 8.0), Point(3.0, 1.0, 4.0) * 2.0)
        assertEquals(Direction(6.0, 2.0, 8.0), Direction(3.0, 1.0, 4.0) * 2.0)
    }

    @Test
    fun cross() {
        assertEquals(Direction(-3.0, -11.0, 5.0), Direction(3.0, 1.0, 4.0) cross Direction(1.0, 2.0, 5.0))
    }

    @Test
    fun length() {
        assertEquals(Math.sqrt(26.0), Direction(3.0, 1.0, 4.0).length, 0.01)
        assertEquals(Math.sqrt(26.0), Point(3.0, 1.0, 4.0).length, 0.01)
    }

    @Test
    fun normalized() {
        assertEquals(1.0, Direction(3.0, 1.0, 4.0).normalized.length, 0.1)
        assertEquals(1.0, Point(3.0, 1.0, 4.0).normalized.length, 0.1)
    }

    @Test
    fun spherical() {
        val v = Direction(1.0, 4.0, 2.0)
        val p = SphericalCoordinate(v)
        assertEquals(4.58, p.r, 0.01)
        assertEquals(1.11, p.theta, 0.01)
        assertEquals(1.32, p.phi, 0.01)
        val u = Direction(p)
        assertEquals(u.x, v.x, 0.01)
        assertEquals(u.y, v.y, 0.01)
        assertEquals(u.z, v.z, 0.01)
    }

}
