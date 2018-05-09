package io.jim.tesserapp.math.vector

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.sqrt

class VectorNTest {

    @Test
    fun construction() {
        Vector4d(1f, 2f, 5f, 4f).apply {
            assertEquals(1f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
            assertEquals(4f, q, 0.1f)

            assertEquals(1f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
            assertEquals(4f, q, 0.1f)
        }
    }

    @Test
    fun load() {
        Vector4d().apply {
            load(1f, 2f, 5f, 4f)

            assertEquals(1f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
            assertEquals(4f, q, 0.1f)

            assertEquals(1f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
            assertEquals(4f, q, 0.1f)
        }
    }

    @Test
    fun addition() {
        val v = Vector4d(1f, 2f, 5f, 0f)
        val u = Vector4d(3f, 1f, 4f, 1f)

        v += u

        v.apply {
            assertEquals(4f, x, 0.1f)
            assertEquals(3f, y, 0.1f)
            assertEquals(9f, z, 0.1f)
            assertEquals(1f, q, 0.1f)
        }
    }

    @Test
    fun subtraction() {
        val v = Vector4d(3f, 1f, 4f, 1f)
        val u = Vector4d(1f, 2f, 5f, 2f)

        v -= u

        v.apply {
            assertEquals(2f, x, 0.1f)
            assertEquals(-1f, y, 0.1f)
            assertEquals(-1f, z, 0.1f)
            assertEquals(-1f, q, 0.1f)
        }
    }

    @Test
    fun scalarMultiplication() {
        val v = VectorN(3f, 1f, 4f, 0f)
        val u = VectorN(1f, 2f, 5f, 3f)
        assertEquals(25f, v * u, 0.1f)
    }

    @Test
    fun scalarMultiplicationEqualsSquaredLength() {
        VectorN(3f, 1f, 4f).apply {
            assertEquals(26f, this * this, 0.1f)
        }
    }

    @Test
    fun division() {
        Vector4d(3f, 1f, 4f, 0f).apply {
            this /= 2f

            assertEquals(1.5f, x, 0.1f)
            assertEquals(0.5f, y, 0.1f)
            assertEquals(2f, z, 0.1f)
            assertEquals(0f, q, 0.1f)
        }
    }

    @Test
    fun scale() {
        Vector4d(3f, 1f, 4f, 4f).apply {
            this *= 2f

            assertEquals(6f, x, 0.1f)
            assertEquals(2f, y, 0.1f)
            assertEquals(8f, z, 0.1f)
            assertEquals(8f, q, 0.1f)
        }
    }

    @Test
    fun cross() {
        (Vector3d()).apply {
            crossed(Vector3d(3f, 1f, 4f), Vector3d(1f, 2f, 5f))

            assertEquals(-3f, x, 0.1f)
            assertEquals(-11f, y, 0.1f)
            assertEquals(5f, z, 0.1f)
        }
    }

    @Test
    fun length() {
        assertEquals(sqrt(26f), VectorN(3f, 1f, 4f).length, 0.1f)
    }

    @Test
    fun normalized() {
        VectorN(3f, 1f, 4f, 5f).apply {
            normalize()

            assertEquals(1f, length, 0.1f)
        }
    }

}
