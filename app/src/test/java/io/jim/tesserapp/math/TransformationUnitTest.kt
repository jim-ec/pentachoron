package io.jim.tesserapp.math

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

/**
 * Homogeneous transform matrix unit test.
 */
class TransformationUnitTest {

    @Test
    fun scaling() {
        val m1 = Matrix().scale(Vector(4.0, 5.0, 1.0, 1.0))
        val v1 = Vector(2.0, 4.0, 0.0, 1.0) * m1
        assertEquals(8.0, v1.x, 0.1)
        assertEquals(20.0, v1.y, 0.1)
        assertEquals(1.0, m1.z.z, 0.01)
    }

    @Test
    fun rotation() {
        val m = Matrix().rotation(1, 3, PI / 2)
        val v = Vector(0.0, 3.0, 0.0, 5.0) * m
        assertEquals(0.0, v.x, 0.1)
        assertEquals(-5.0, v.y, 0.1)
        assertEquals(0.0, v.z, 0.1)
        assertEquals(3.0, v.w, 0.1)
    }

    @Test
    fun translation() {
        val m = Matrix().translation(Vector(2.0, 1.0, 0.0, 1.0))

        val p = Vector(2.0, 2.0, 0.0, 1.0) * m
        assertEquals(4.0, p.x, 0.1)
        assertEquals(3.0, p.y, 0.1)

        val d = Vector(2.0, 2.0, 0.0, 0.0) * m
        assertEquals(2.0, d.x, 0.1)
        assertEquals(2.0, d.y, 0.1)
    }

    @Test
    fun spaces() {
        val i = Matrix()
        val m = Matrix().space(
                Vector(1.0, 0.0, 0.0, 0.0),
                Vector(0.0, 1.0, 0.0, 0.0),
                Vector(0.0, 0.0, 1.0, 0.0),
                Vector(0.0, 0.0, 0.0, 1.0)
        )
        assertEquals(1.0, m.q.w, 0.01)
        m.forEachCoefficient { r, c -> assertEquals(i[r][c], m[r][c], 0.01) }

        val s = Matrix().scale(Vector(5.0, 5.0, 5.0, 1.0))
        val n = Matrix().space(
                Vector(5.0, 0.0, 0.0, 0.0),
                Vector(0.0, 5.0, 0.0, 0.0),
                Vector(0.0, 0.0, 5.0, 0.0),
                Vector(0.0, 0.0, 0.0, 1.0)
        )
        assertEquals(1.0, s.q.w, 0.01)
        assertEquals(1.0, n.q.w, 0.01)
        s.forEachCoefficient { r, c -> assertEquals(s[r][c], n[r][c], 0.01) }
    }

    @Test
    fun perspective() {
        assertEquals(2.0 / 10.0,
                (Vector(2.0, 3.0, -10.0, 1.0) * Matrix().perspective()).apply { perspectiveDivide() }.x,
                0.1)

        val m = Matrix().perspective(5.0, 10.0)

        (Vector(2.0, 3.0, -10.0, 1.0) * m).apply {
            perspectiveDivide()
            assertEquals(1.0, z, 0.1)
            assertEquals(2.0 / 10.0, x, 0.1)
            assertEquals(3.0 / 10.0, y, 0.1)
        }

        (Vector(2.0, 3.0, -5.0, 1.0) * m).apply {
            perspectiveDivide()
            assertEquals(0.0, z, 0.1)
            assertEquals(2.0 / 5.0, x, 0.1)
            assertEquals(3.0 / 5.0, y, 0.1)
        }

        (Vector(2.0, 3.0, -7.0, 1.0) * m).apply {
            perspectiveDivide()
            assertTrue(0.0 < z && z < 1.0)
            assertEquals(2.0 / 7.0, x, 0.1)
            assertEquals(3.0 / 7.0, y, 0.1)
        }

        (Vector(2.0, 3.0, -2.0, 1.0) * m).apply {
            perspectiveDivide()
            assertTrue(z < 0.0)
            assertEquals(2.0 / 2.0, x, 0.1)
            assertEquals(3.0 / 2.0, y, 0.1)
        }
    }

    @Test
    fun combination() {
        val m = Matrix()
        m.multiplicationFrom(
                Matrix().scale(Vector(2.0, 2.0, 2.0, 1.0)),
                Matrix().translation(Vector(4.0, 0.0, 0.0, 0.0))
        )
        val p = Vector(1.0, 2.0, 3.0, 1.0) * m
        assertEquals(6.0, p.x, 0.1)
        assertEquals(4.0, p.y, 0.1)
        assertEquals(6.0, p.z, 0.1)
    }

    @Test
    fun lookAt() {
        val m = Matrix().lookAt(
                Vector(-2.0, 0.0, -1.0, 1.0),
                Vector(2.0, -1.0, 1.0, 1.0),
                Vector(0.0, 0.0, 1.0, 0.0)
        )

        m.apply {
            // Check that all matrix axis are perpendicular to each other:
            assertEquals(0.0, forward * right, 0.1)
            assertEquals(0.0, right * up, 0.1)
            assertEquals(0.0, up * forward, 0.1)

            // Check the all matrix axis are unit vectors:
            assertEquals(1.0, forward.length, 0.1)
            assertEquals(1.0, right.length, 0.1)
            assertEquals(1.0, up.length, 0.1)
        }
    }

    @Test
    fun lookAt_3dToOrigin() {
        val m = Matrix().lookAt(
                Vector(2.0, 2.0, 2.0, 1.0),
                Vector(0.0, 0.0, 0.0, 1.0),
                Vector(0.0, 1.0, 0.0, 0.0))

        (Vector(0.0, 0.0, 0.0, 1.0) * m).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertTrue(z < 0.0)
        }
    }

}