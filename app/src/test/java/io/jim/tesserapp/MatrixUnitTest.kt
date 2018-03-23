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
        val m = Matrix(4)
        assertEquals(4, m.size)
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
    fun multiplication() {
        val m = Matrix(2, listOf(2.0, 3.0, 4.0, 5.0))
        val n = Matrix(2, listOf(6.0, 7.0, 8.0, 9.0))
        val u = m * n
        assertEquals(2.0 * 6.0 + 3.0 * 8.0, u[0][0], 0.1)
        assertEquals(2.0 * 7.0 + 3.0 * 9.0, u[0][1], 0.1)
        assertEquals(4.0 * 6.0 + 5.0 * 8.0, u[1][0], 0.1)
        assertEquals(4.0 * 7.0 + 5.0 * 9.0, u[1][1], 0.1)
    }

    @Test
    fun vectorMultiplication() {
        val m = Matrix(2, listOf(0.0, 1.0, -1.0, 0.0))
        val v = Vector(2.0, 1.0) * m
        assertEquals(-1.0, v.x, 0.1)
        assertEquals(2.0, v.y, 0.1)
    }

}
