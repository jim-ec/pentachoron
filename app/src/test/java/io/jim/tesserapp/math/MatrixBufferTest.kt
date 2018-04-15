package io.jim.tesserapp.math

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class MatrixBufferTest {

    private val buffer = MatrixBuffer(3)
    private val memory = buffer.MemorySpace(0, 3)

    @Test
    fun initialization() {
        for (matrix in 0 until 2) {
            for (row in 0 until MatrixBuffer.MATRIX_COLUMNS) {
                for (col in 0 until MatrixBuffer.MATRIX_COLUMNS) {
                    assertEquals(if (row == col) 1f else 0f, memory[matrix, row, col], 0.1f)
                }
            }
        }
    }

    @Test
    fun identity() {
        memory.rotation(1, 0, 2, PI)
        memory.identity(1)
        for (row in 0 until MatrixBuffer.MATRIX_COLUMNS) {
            for (col in 0 until MatrixBuffer.MATRIX_COLUMNS) {
                assertEquals(if (row == col) 1f else 0f, memory[1, row, col], 0.1f)
            }
        }
    }

    @Test
    fun copy() {
        memory.space(1,
                Vector(1.0, 2.0, 3.0, 4.0),
                Vector(5.0, 6.0, 7.0, 8.0),
                Vector(9.0, 10.0, 11.0, 12.0),
                Vector(13.0, 14.0, 15.0, 16.0))

        memory.copy(2, 1)

        memory.multiply(Vector(1.0, 2.0, 3.0, 4.0), 2).apply {
            assertEquals(90.0, x, 0.1)
            assertEquals(100.0, y, 0.1)
            assertEquals(110.0, z, 0.1)
            assertEquals(120.0, w, 0.1)
        }
    }

    @Test
    fun copyFromOtherBuffer() {
        val otherBuffer = MatrixBuffer(3)

        otherBuffer.MemorySpace(1, 2).space(1,
                Vector(1.0, 2.0, 3.0, 4.0),
                Vector(5.0, 6.0, 7.0, 8.0),
                Vector(9.0, 10.0, 11.0, 12.0),
                Vector(13.0, 14.0, 15.0, 16.0))

        memory.copy(2, 0, otherBuffer.MemorySpace(2, 1))

        memory.multiply(Vector(1.0, 2.0, 3.0, 4.0), 2).apply {
            assertEquals(90.0, x, 0.1)
            assertEquals(100.0, y, 0.1)
            assertEquals(110.0, z, 0.1)
            assertEquals(120.0, w, 0.1)
        }
    }

    @Test
    fun space() {
        memory.space(1,
                Vector(1.0, 2.0, 3.0, 4.0),
                Vector(5.0, 6.0, 7.0, 8.0),
                Vector(9.0, 10.0, 11.0, 12.0),
                Vector(13.0, 14.0, 15.0, 16.0))

        memory.multiply(Vector(1.0, 2.0, 3.0, 4.0), 1).apply {
            assertEquals(90.0, x, 0.1)
            assertEquals(100.0, y, 0.1)
            assertEquals(110.0, z, 0.1)
            assertEquals(120.0, w, 0.1)
        }
    }

    @Test
    fun multiplyMatrices() {
        memory.space(0,
                Vector(2.0, 3.0, 0.0, 0.0),
                Vector(4.0, 5.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0))

        memory.space(1,
                Vector(6.0, 7.0, 0.0, 0.0),
                Vector(8.0, 9.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0),
                Vector(0.0, 0.0, 0.0, 0.0))

        memory.multiply(0, 1, 2)
        assertEquals(2f * 6 + 3 * 8, memory[2, 0, 0], 0.1f)
        assertEquals(2f * 7 + 3 * 9, memory[2, 0, 1], 0.1f)
        assertEquals(4f * 6 + 5 * 8, memory[2, 1, 0], 0.1f)
        assertEquals(4f * 7 + 5 * 9, memory[2, 1, 1], 0.1f)
    }

    @Test
    fun scale() {
        memory.scale(1, Vector(1.0, 2.0, 3.0, 4.0))
        memory.multiply(Vector(1.0, 2.0, 3.0, 4.0), 1).apply {
            assertEquals(1.0, x, 0.1)
            assertEquals(4.0, y, 0.1)
            assertEquals(9.0, z, 0.1)
            assertEquals(16.0, w, 0.1)
        }
    }

    @Test
    fun rotation() {
        memory.rotation(2, 1, 3, PI / 2)
        memory.multiply(Vector(0.0, 3.0, 0.0, 5.0), 2).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(-5.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(3.0, w, 0.1)
        }
    }

    @Test
    fun translation() {
        memory.translation(2, Vector(1.0, 2.0, 3.0, 1.0))
        memory.multiply(Vector(1.0, 2.0, 3.0, 1.0), 2).apply {
            assertEquals(2.0, x, 0.1)
            assertEquals(4.0, y, 0.1)
            assertEquals(6.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }
    }

    @Test
    fun transpose() {
        memory.space(1,
                Vector(1.0, 5.0, 9.0, 13.0),
                Vector(2.0, 6.0, 10.0, 14.0),
                Vector(3.0, 7.0, 11.0, 15.0),
                Vector(4.0, 8.0, 12.0, 16.0))

        memory.transpose(1)

        memory.multiply(Vector(1.0, 2.0, 3.0, 4.0), 1).apply {
            assertEquals(90.0, x, 0.1)
            assertEquals(100.0, y, 0.1)
            assertEquals(110.0, z, 0.1)
            assertEquals(120.0, w, 0.1)
        }
    }

    @Test
    fun perspective2D() {
        memory.perspective2D(0, 5f, 10f)

        memory.multiply(Vector(2.0, 3.0, -10.0, 1.0), 0).apply {
            perspectiveDivide()
            assertEquals(1.0, z, 0.1)
            assertEquals(2.0 / 10.0, x, 0.1)
            assertEquals(3.0 / 10.0, y, 0.1)
        }

        memory.multiply(Vector(2.0, 3.0, -5.0, 1.0), 0).apply {
            perspectiveDivide()
            assertEquals(0.0, z, 0.1)
            assertEquals(2.0 / 5.0, x, 0.1)
            assertEquals(3.0 / 5.0, y, 0.1)
        }

        memory.multiply(Vector(2.0, 3.0, -7.0, 1.0), 0).apply {
            perspectiveDivide()
            assertTrue(0.0 < z && z < 1.0)
            assertEquals(2.0 / 7.0, x, 0.1)
            assertEquals(3.0 / 7.0, y, 0.1)
        }

        memory.multiply(Vector(2.0, 3.0, -2.0, 1.0), 0).apply {
            perspectiveDivide()
            Assert.assertTrue(z < 0.0)
            assertEquals(2.0 / 2.0, x, 0.1)
            assertEquals(3.0 / 2.0, y, 0.1)
        }
    }

    @Test
    fun lookAt() {
        memory.lookAt(0,
                Vector(2.0, 2.0, 2.0, 1.0),
                Vector(0.0, 0.0, 0.0, 1.0),
                Vector(0.0, 1.0, 0.0, 0.0)
        )

        // Check that all matrix axis are perpendicular to each other:
        assertEquals(0.0, memory[0, MatrixBuffer.FORWARD_ROW] * memory[0, MatrixBuffer.RIGHT_ROW], 0.1)
        assertEquals(0.0, memory[0, MatrixBuffer.RIGHT_ROW] * memory[0, MatrixBuffer.UP_ROW], 0.1)
        assertEquals(0.0, memory[0, MatrixBuffer.UP_ROW] * memory[0, MatrixBuffer.FORWARD_ROW], 0.1)

        // Check the all matrix axis are unit vectors:
        assertEquals(1.0, memory[0, MatrixBuffer.FORWARD_ROW].length, 0.1)
        assertEquals(1.0, memory[0, MatrixBuffer.RIGHT_ROW].length, 0.1)
        assertEquals(1.0, memory[0, MatrixBuffer.UP_ROW].length, 0.1)

        memory.multiply(Vector(0.0, 0.0, 0.0, 1.0), 0).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertTrue(z < 0.0)
        }
    }

}