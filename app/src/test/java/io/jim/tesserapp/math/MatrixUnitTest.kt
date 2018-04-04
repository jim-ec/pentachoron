package io.jim.tesserapp.math

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Matrix unit test.
 */
class MatrixUnitTest {

    @Test
    fun construction() {
        val m = Matrix()
        assertEquals(1.0, m[0][0], 0.1)
        assertEquals(1.0, m[1][1], 0.1)
        assertEquals(1.0, m[2][2], 0.1)
        assertEquals(1.0, m[3][3], 0.1)
    }

    @Test
    fun indexAccess() {
        val m = Matrix()
        m.y.y = 3.0

        assertEquals(1.0, m.x.x, 0.1)
        assertEquals(0.0, m.x.y, 0.1)
        assertEquals(0.0, m.x.z, 0.1)
        assertEquals(0.0, m.x.w, 0.1)

        assertEquals(0.0, m.y.x, 0.1)
        assertEquals(3.0, m.y.y, 0.1)
        assertEquals(0.0, m.y.z, 0.1)
        assertEquals(0.0, m.y.w, 0.1)

        assertEquals(0.0, m.z.x, 0.1)
        assertEquals(0.0, m.z.y, 0.1)
        assertEquals(1.0, m.z.z, 0.1)
        assertEquals(0.0, m.z.w, 0.1)

        assertEquals(0.0, m.q.x, 0.1)
        assertEquals(0.0, m.q.y, 0.1)
        assertEquals(0.0, m.q.z, 0.1)
        assertEquals(1.0, m.q.w, 0.1)
    }

    @Test
    fun forEachCoefficient() {
        val m = Matrix()
        m.forEachCoefficient(startRow = 1, rows = 2, startCol = 2) { r, c ->
            m[r][c] = r + c.toDouble()
        }
    }

    @Test
    fun spaceDefinition() {
        val m = Matrix().space(
                Vector(-1.0, 0.0, 0.0, 0.0),
                Vector(0.0, 2.0, 0.0, 0.0),
                Vector(0.0, 0.0, 1.0, 0.0),
                Vector(3.0, 4.0, 0.0, 0.0))

        (Vector(1.0, 1.0, 0.0, 0.0) * m).apply {
            assertEquals(-1.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(0.0, w, 0.1)
        }
    }

    @Test
    fun transpose() {
        val m = Matrix().space(
                Vector(1.0, 2.0, 0.0, 0.0),
                Vector(3.0, 4.0, 0.0, 0.0),
                Vector(5.0, 6.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0))
        m.transpose()

        assertEquals(1.0, m.x.x, 0.1)
        assertEquals(3.0, m.x.y, 0.1)
        assertEquals(2.0, m.y.x, 0.1)
        assertEquals(4.0, m.y.y, 0.1)
    }

    @Test
    fun multiplication() {
        val m = Matrix().space(
                Vector(2.0, 3.0, 0.0, 0.0),
                Vector(4.0, 5.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0))

        val n = Matrix().space(
                Vector(6.0, 7.0, 0.0, 0.0),
                Vector(8.0, 9.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0))

        val u = Matrix().multiplicationFrom(m, n)
        assertEquals(2.0 * 6.0 + 3.0 * 8.0, u.x.x, 0.1)
        assertEquals(2.0 * 7.0 + 3.0 * 9.0, u.x.y, 0.1)
        assertEquals(4.0 * 6.0 + 5.0 * 8.0, u.y.x, 0.1)
        assertEquals(4.0 * 7.0 + 5.0 * 9.0, u.y.y, 0.1)
    }

    @Test
    fun vectorMultiplication() {
        val m = Matrix().space(
                Vector(0.0, 1.0, 0.0, 0.0),
                Vector(-1.0, 0.0, 0.0, 0.0),
                Vector(0.0, 0.0, 1.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0))
        val v = Vector(2.0, 1.0, 0.0, 0.0) * m
        assertEquals(-1.0, v.x, 0.1)
        assertEquals(2.0, v.y, 0.1)
    }

}
