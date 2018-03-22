package io.jim.tesserapp

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.PI

/**
 * Vector unit test.
 */
class MatrixUnitTest {

    @Test
    fun construction() {
        val m = Matrix.identity(4)
        assertEquals(4, m.size)
    }

    @Test
    fun compatibility() {
        val m = Matrix.identity(4)
        val n = Matrix.identity(4)
        val u = Matrix.identity(5)
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

    @Test
    fun shearing() {
        val m = Matrix.shear(2, 0, 0.0, 1, 0.5)
        val v = Vector(2.0, 2.0) * m
        assertEquals(3.0, v.x, 0.1)
        assertEquals(2.0, v.y, 0.1)
    }

    @Test
    fun scaling() {
        val m = Matrix.scale(2, 3.0)
        val v = Vector(2.0, 4.0) * m
        assertEquals(6.0, v.x, 0.1)
        assertEquals(12.0, v.y, 0.1)
    }

    @Test
    fun rotation() {
        val m = Matrix.rotation(4, 1, 3, PI / 2)
        val v = Vector(0.0, 3.0, 0.0, 5.0) * m
        assertEquals(0.0, v.x, 0.1)
        assertEquals(-5.0, v.y, 0.1)
        assertEquals(0.0, v.z, 0.1)
        assertEquals(3.0, v.w, 0.1)
    }

    @Test
    fun transformation() {
        val m = Matrix.translation(3, Vector(2.0, 1.0))
        val p = Vector(2.0, 2.0, 1.0) * m
        assertEquals(4.0, p.x, 0.1)
        assertEquals(3.0, p.y, 0.1)
    }

    @Test
    fun transpose() {
        val m = Matrix(2, listOf(
                1.0, 2.0,
                3.0, 4.0))
        val n = m.transposed()
        assertEquals(1.0, n[0][0], 0.1)
        assertEquals(3.0, n[0][1], 0.1)
        assertEquals(2.0, n[1][0], 0.1)
        assertEquals(4.0, n[1][1], 0.1)
    }

}
