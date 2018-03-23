package io.jim.tesserapp

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import org.junit.Assert.assertEquals
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
    }

    @Test
    fun scaling() {
        val m = Matrix.scale(3, 3.0)
        val v = Vector.point(2.0, 4.0) * m
        assertEquals(6.0, v.x, 0.1)
        assertEquals(12.0, v.y, 0.1)
    }

    @Test
    fun rotation() {
        val m = Matrix.rotation(5, 1, 3, PI / 2)
        val v = Vector.point(0.0, 3.0, 0.0, 5.0) * m
        assertEquals(0.0, v.x, 0.1)
        assertEquals(-5.0, v.y, 0.1)
        assertEquals(0.0, v.z, 0.1)
        assertEquals(3.0, v.w, 0.1)
    }

    @Test
    fun translation() {
        val m = Matrix.translation(3, Vector(2.0, 1.0))

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
        m.forEachCoefficient { r, c -> assertEquals(i[r][c], m[r][c], 0.01) }

        val t = Matrix.scale(4, 5.0)
        val n = Matrix.space(4, listOf(
                Vector(5.0, 0.0, 0.0),
                Vector(0.0, 5.0, 0.0),
                Vector(0.0, 0.0, 5.0)
        ))
        t.forEachCoefficient { r, c -> assertEquals(t[r][c], n[r][c], 0.01) }
    }

}
