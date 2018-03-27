package io.jim.tesserapp

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

/**
 * Homogeneous transform matrix unit test.
 */
class TransformationUnitTest {

    @Test
    fun shearing() {
        val m = Matrix.shear(3, 0, 0.0, 1, 0.5)
        val v = Vector.point(2.0, 2.0) * m
        assertEquals(3.0, v.x, 0.1)
        assertEquals(2.0, v.y, 0.1)
        assertEquals(1.0, m.last().last(), 0.01)
    }

    @Test
    fun scaling() {
        val m = Matrix.scale(3, 3.0)
        val v = Vector.point(2.0, 4.0) * m
        assertEquals(6.0, v.x, 0.1)
        assertEquals(12.0, v.y, 0.1)
        assertEquals(1.0, m.last().last(), 0.01)

        val m1 = Matrix.scale(3, Vector(4.0, 5.0))
        val v1 = Vector.direction(2.0, 4.0) * m1
        assertEquals(8.0, v1.x, 0.1)
        assertEquals(20.0, v1.y, 0.1)
        assertEquals(1.0, m1.last().last(), 0.01)
    }

    @Test
    fun rotation() {
        val m = Matrix.rotation(5, 1, 3, PI / 2)
        val v = Vector.point(0.0, 3.0, 0.0, 5.0) * m
        assertEquals(0.0, v.x, 0.1)
        assertEquals(-5.0, v.y, 0.1)
        assertEquals(0.0, v.z, 0.1)
        assertEquals(3.0, v.w, 0.1)
        assertEquals(1.0, m.last().last(), 0.01)
    }

    @Test
    fun translation() {
        val m = Matrix.translation(3, Vector(2.0, 1.0))
        assertEquals(1.0, m.last().last(), 0.01)

        val p = Vector.point(2.0, 2.0) * m
        assertEquals(4.0, p.x, 0.1)
        assertEquals(3.0, p.y, 0.1)

        val d = Vector.direction(2.0, 2.0) * m
        assertEquals(2.0, d.x, 0.1)
        assertEquals(2.0, d.y, 0.1)
    }

    @Test
    fun spaces() {
        val i = Matrix(4)
        val m = Matrix.space(4, listOf(
                Vector(1.0, 0.0, 0.0),
                Vector(0.0, 1.0, 0.0),
                Vector(0.0, 0.0, 1.0)
        ))
        assertEquals(1.0, m.last().last(), 0.01)
        m.forEachCoefficient { r, c -> assertEquals(i[r][c], m[r][c], 0.01) }

        val s = Matrix.scale(4, 5.0)
        val n = Matrix.space(4, listOf(
                Vector(5.0, 0.0, 0.0),
                Vector(0.0, 5.0, 0.0),
                Vector(0.0, 0.0, 5.0)
        ))
        assertEquals(1.0, s.last().last(), 0.01)
        assertEquals(1.0, n.last().last(), 0.01)
        s.forEachCoefficient { r, c -> assertEquals(s[r][c], n[r][c], 0.01) }
    }

    @Test
    fun perspective() {
        val m = Matrix.perspective(4)
        val r = Matrix.perspective(4, 5.0, 10.0)
        val v = Vector.point(2.0, 3.0, -10.0)

        assertTrue((v * m).w != 1.0)
        assertTrue((v * m).w != 0.0)

        val vProjected = (v * m).perspectiveProjection
        assertEquals(2.0 / 10.0, vProjected.x, 0.1)

        val vRemapped = (v * r).perspectiveProjection
        assertEquals(vRemapped.z, 1.0, 0.1)
    }

    @Test
    fun combination() {
        val m = Matrix.scale(4, 2.0) * Matrix.translation(4, Vector(4.0, 0.0, 0.0))
        val p = Vector.point(1.0, 2.0, 3.0) * m
        assertEquals(6.0, p.x, 0.1)
        assertEquals(4.0, p.y, 0.1)
        assertEquals(6.0, p.z, 0.1)
    }

}
