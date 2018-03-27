package io.jim.tesserapp

import io.jim.tesserapp.math.Direction
import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Point
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
        val m = Matrix.scale(2, 3.0)
        val v = Point(2.0, 4.0) * m
        assertEquals(6.0, v.x, 0.1)
        assertEquals(12.0, v.y, 0.1)
        assertEquals(1.0, m[2][2], 0.01)

        val m1 = Matrix.scale(2, Point(4.0, 5.0))
        val v1 = Direction(2.0, 4.0) * m1
        assertEquals(8.0, v1.x, 0.1)
        assertEquals(20.0, v1.y, 0.1)
        assertEquals(1.0, m1[2][2], 0.01)
    }

    @Test
    fun rotation() {
        val m = Matrix.rotation(4, 1, 3, PI / 2)
        val v = Direction(0.0, 3.0, 0.0, 5.0) * m
        assertEquals(0.0, v.x, 0.1)
        assertEquals(-5.0, v.y, 0.1)
        assertEquals(0.0, v.z, 0.1)
        assertEquals(3.0, v.q, 0.1)
        assertEquals(1.0, m[4][4], 0.01)
    }

    @Test
    fun translation() {
        val m = Matrix.translation(2, Direction(2.0, 1.0))

        val p = Point(2.0, 2.0) * m
        assertEquals(4.0, p.x, 0.1)
        assertEquals(3.0, p.y, 0.1)

        val d = Direction(2.0, 2.0) * m
        assertEquals(2.0, d.x, 0.1)
        assertEquals(2.0, d.y, 0.1)
    }

    @Test
    fun spaces() {
        val i = Matrix(3)
        val m = Matrix.space(3, Point(0.0, 0.0, 0.0),
                Direction(1.0, 0.0, 0.0),
                Direction(0.0, 1.0, 0.0),
                Direction(0.0, 0.0, 1.0)
        )
        assertEquals(1.0, m[3][3], 0.01)
        m.forEachCoefficient { r, c -> assertEquals(i[r][c], m[r][c], 0.01) }

        val s = Matrix.scale(3, 5.0)
        val n = Matrix.space(3, Point(0.0, 0.0, 0.0),
                Direction(5.0, 0.0, 0.0),
                Direction(0.0, 5.0, 0.0),
                Direction(0.0, 0.0, 5.0)
        )
        assertEquals(1.0, s[3][3], 0.01)
        assertEquals(1.0, n[3][3], 0.01)
        s.forEachCoefficient { r, c -> assertEquals(s[r][c], n[r][c], 0.01) }
    }

    @Test
    fun perspective() {
        assertEquals(2.0 / 10.0, (Point(2.0, 3.0, -10.0) * Matrix.perspective(3)).x, 0.1)

        val m = Matrix.perspective(3, 5.0, 10.0)

        (Point(2.0, 3.0, -10.0) * m).apply {
            assertEquals(1.0, z, 0.1)
            assertEquals(2.0 / 10.0, x, 0.1)
            assertEquals(3.0 / 10.0, y, 0.1)
        }

        (Point(2.0, 3.0, -5.0) * m).apply {
            assertEquals(0.0, z, 0.1)
            assertEquals(2.0 / 5.0, x, 0.1)
            assertEquals(3.0 / 5.0, y, 0.1)
        }

        (Point(2.0, 3.0, -7.0) * m).apply {
            assertTrue(0.0 < z && z < 1.0)
            assertEquals(2.0 / 7.0, x, 0.1)
            assertEquals(3.0 / 7.0, y, 0.1)
        }

        (Point(2.0, 3.0, -2.0) * m).apply {
            assertTrue(z < 0.0)
            assertEquals(2.0 / 2.0, x, 0.1)
            assertEquals(3.0 / 2.0, y, 0.1)
        }
    }

    @Test
    fun combination() {
        val m = Matrix.scale(3, 2.0) * Matrix.translation(3, Direction(4.0, 0.0, 0.0))
        val p = Point(1.0, 2.0, 3.0) * m
        assertEquals(6.0, p.x, 0.1)
        assertEquals(4.0, p.y, 0.1)
        assertEquals(6.0, p.z, 0.1)
    }

}
