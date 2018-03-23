package io.jim.tesserapp

import io.jim.tesserapp.math.*
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

/**
 * Homogeneous transform matrix unit test.
 */
class TransformationUnitTest {

    @Test
    fun construction() {
        val h = HomogeneousMatrix(3)
        assertEquals(3, h.dimension)
        assertEquals(4, h.size)
    }

    @Test
    fun shearing() {
        val m = HomogeneousMatrix.shear(2, 0, 0.0, 1, 0.5)
        val v = Point(2.0, 2.0) * m
        assertEquals(3.0, v.x, 0.1)
        assertEquals(2.0, v.y, 0.1)
    }

    @Test
    fun scaling() {
        val m = HomogeneousMatrix.scale(2, 3.0)
        val v = Point(2.0, 4.0) * m
        assertEquals(6.0, v.x, 0.1)
        assertEquals(12.0, v.y, 0.1)
    }

    @Test
    fun rotation() {
        val m = HomogeneousMatrix.rotation(4, 1, 3, PI / 2)
        val v = Point(0.0, 3.0, 0.0, 5.0) * m
        assertEquals(0.0, v.x, 0.1)
        assertEquals(-5.0, v.y, 0.1)
        assertEquals(0.0, v.z, 0.1)
        assertEquals(3.0, v.w, 0.1)
    }

    @Test
    fun translation() {
        val m = HomogeneousMatrix.translation(2, Vector(2.0, 1.0))

        val p = Point(2.0, 2.0) * m
        assertEquals(4.0, p.x, 0.1)
        assertEquals(3.0, p.y, 0.1)

        val d = Direction(2.0, 2.0) * m
        assertEquals(2.0, d.x, 0.1)
        assertEquals(2.0, d.y, 0.1)
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

    @Test
    fun spaces() {
        val i = HomogeneousMatrix(3)
        val m = HomogeneousMatrix.space(3, listOf(
                Vector(1.0, 0.0, 0.0),
                Vector(0.0, 1.0, 0.0),
                Vector(0.0, 0.0, 1.0)
        ))
        m.forEachCoefficient { r, c -> assertEquals(i[r][c], m[r][c], 0.01) }

        val t = HomogeneousMatrix.scale(3, 5.0)
        val n = HomogeneousMatrix.space(3, listOf(
                Vector(5.0, 0.0, 0.0),
                Vector(0.0, 5.0, 0.0),
                Vector(0.0, 0.0, 5.0)
        ))
        t.forEachCoefficient { r, c -> assertEquals(t[r][c], n[r][c], 0.01) }
    }

}
