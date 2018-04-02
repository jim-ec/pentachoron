package io.jim.tesserapp

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import org.junit.Assert.*
import org.junit.Test

/**
 * Matrix unit test.
 */
class MatrixUnitTest {

    @Test
    fun construction() {
        val m = Matrix(3)
        assertEquals(3, m.dimension)
    }

    @Test
    fun indexAccess() {
        val m = Matrix(3)
        m.y.y = 3.0

        assertEquals(1.0, m.x.x, 0.1)
        assertEquals(0.0, m.x.y, 0.1)
        assertEquals(0.0, m.x.z, 0.1)
        assertEquals(0.0, m.x.q, 0.1)

        assertEquals(0.0, m.y.x, 0.1)
        assertEquals(3.0, m.y.y, 0.1)
        assertEquals(0.0, m.y.z, 0.1)
        assertEquals(0.0, m.y.q, 0.1)

        assertEquals(0.0, m.z.x, 0.1)
        assertEquals(0.0, m.z.y, 0.1)
        assertEquals(1.0, m.z.z, 0.1)
        assertEquals(0.0, m.z.q, 0.1)

        assertEquals(0.0, m.q.x, 0.1)
        assertEquals(0.0, m.q.y, 0.1)
        assertEquals(0.0, m.q.z, 0.1)
        assertEquals(1.0, m.q.q, 0.1)
    }

    @Test
    fun forEachCoefficient() {
        val m = Matrix(3)
        m.forEachCoefficient(startRow = 1, rows = 2, startCol = 2) { r, c ->
            m[r][c] = r + c.toDouble()
        }
        println(m)
    }

    @Test
    fun compatibility() {
        val m = Matrix(4)
        val n = Matrix(4)
        val u = Matrix(5)
        val v = Vector(1.0, 2.0, 3.0, 4.0)
        assertTrue(m compatible n)
        assertTrue(n compatible m)
        assertTrue(m compatible v)
        assertFalse(m compatible u)
        assertFalse(n compatible u)
        assertFalse(u compatible m)
        assertFalse(u compatible n)
    }

    @Test
    fun spaceDefinition() {
        val m = Matrix(2).space(Vector(3.0, 4.0),
                Vector(-1.0, 0.0),
                Vector(0.0, 2.0))
        val p = Vector(1.0, 1.0) applyPoint m
        assertEquals(2.0, p.x, 0.1)
    }

    @Test
    fun transpose() {
        val m = Matrix(2).space(Vector(0.0, 0.0),
                Vector(1.0, 2.0),
                Vector(3.0, 4.0))
        val n = Matrix(m).apply { transpose() }
        assertEquals(1.0, n.x.x, 0.1)
        assertEquals(3.0, n.x.y, 0.1)
        assertEquals(2.0, n.y.x, 0.1)
        assertEquals(4.0, n.y.y, 0.1)
    }

    @Test
    fun multiplication() {
        val m = Matrix(2).space(Vector(0.0, 0.0),
                Vector(2.0, 3.0),
                Vector(4.0, 5.0))
        val n = Matrix(2).space(Vector(0.0, 0.0),
                Vector(6.0, 7.0),
                Vector(8.0, 9.0))
        val u = m * n
        assertEquals(2.0 * 6.0 + 3.0 * 8.0, u.x.x, 0.1)
        assertEquals(2.0 * 7.0 + 3.0 * 9.0, u.x.y, 0.1)
        assertEquals(4.0 * 6.0 + 5.0 * 8.0, u.y.x, 0.1)
        assertEquals(4.0 * 7.0 + 5.0 * 9.0, u.y.y, 0.1)
    }

    @Test
    fun vectorMultiplication() {
        val m = Matrix(2).space(Vector(0.0, 0.0),
                Vector(0.0, 1.0),
                Vector(-1.0, 0.0))
        val v = Vector(2.0, 1.0) applyPoint m
        assertEquals(v.dimension, 2)
        assertEquals(-1.0, v.x, 0.1)
        assertEquals(2.0, v.y, 0.1)
    }

}
