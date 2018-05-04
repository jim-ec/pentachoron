package io.jim.tesserapp.math

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
        memory.rotation(1, 0, 2, Pi)
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
                Vector(1f, 2f, 3f, 4f),
                Vector(5f, 6f, 7f, 8f),
                Vector(9f, 10f, 11f, 12f),
                Vector(13f, 14f, 15f, 16f))

        memory.copy(2, 1)

        memory.multiply(Vector(1f, 2f, 3f, 4f), 2).apply {
            assertEquals(90f, x, 0.1f)
            assertEquals(100f, y, 0.1f)
            assertEquals(110f, z, 0.1f)
            assertEquals(120f, w, 0.1f)
        }
    }

    @Test
    fun copyFromOtherBuffer() {
        val otherBuffer = MatrixBuffer(3)

        otherBuffer.MemorySpace(1, 2).space(1,
                Vector(1f, 2f, 3f, 4f),
                Vector(5f, 6f, 7f, 8f),
                Vector(9f, 10f, 11f, 12f),
                Vector(13f, 14f, 15f, 16f))

        memory.copy(2, 0, otherBuffer.MemorySpace(2, 1))

        memory.multiply(Vector(1f, 2f, 3f, 4f), 2).apply {
            assertEquals(90f, x, 0.1f)
            assertEquals(100f, y, 0.1f)
            assertEquals(110f, z, 0.1f)
            assertEquals(120f, w, 0.1f)
        }
    }

    @Test
    fun space() {
        memory.space(1,
                Vector(1f, 2f, 3f, 4f),
                Vector(5f, 6f, 7f, 8f),
                Vector(9f, 10f, 11f, 12f),
                Vector(13f, 14f, 15f, 16f))

        memory.multiply(Vector(1f, 2f, 3f, 4f), 1).apply {
            assertEquals(90f, x, 0.1f)
            assertEquals(100f, y, 0.1f)
            assertEquals(110f, z, 0.1f)
            assertEquals(120f, w, 0.1f)
        }
    }

    @Test
    fun multiplyMatrices() {
        memory.space(0,
                Vector(2f, 3f, 0f, 0f),
                Vector(4f, 5f, 0f, 0f),
                Vector(0f, 0f, 0f, 0f),
                Vector(0f, 0f, 0f, 0f))

        memory.space(1,
                Vector(6f, 7f, 0f, 0f),
                Vector(8f, 9f, 0f, 0f),
                Vector(0f, 0f, 0f, 0f),
                Vector(0f, 0f, 0f, 0f))

        memory.multiply(0, 1, 2)
        assertEquals(2f * 6 + 3 * 8, memory[2, 0, 0], 0.1f)
        assertEquals(2f * 7 + 3 * 9, memory[2, 0, 1], 0.1f)
        assertEquals(4f * 6 + 5 * 8, memory[2, 1, 0], 0.1f)
        assertEquals(4f * 7 + 5 * 9, memory[2, 1, 1], 0.1f)
    }

    @Test
    fun scale() {
        memory.scale(1, Vector(1f, 2f, 3f, 4f))
        memory.multiply(Vector(1f, 2f, 3f, 4f), 1).apply {
            assertEquals(1f, x, 0.1f)
            assertEquals(4f, y, 0.1f)
            assertEquals(9f, z, 0.1f)
            assertEquals(16f, w, 0.1f)
        }
    }

    @Test
    fun rotation() {
        memory.rotation(2, 1, 3, Pi / 2)
        memory.multiply(Vector(0f, 3f, 0f, 5f), 2).apply {
            assertEquals(0f, x, 0.1f)
            assertEquals(-5f, y, 0.1f)
            assertEquals(0f, z, 0.1f)
            assertEquals(3f, w, 0.1f)
        }
    }

    @Test
    fun translation() {
        memory.translation(2, Vector(1f, 2f, 3f, 1f))
        memory.multiply(Vector(1f, 2f, 3f, 1f), 2).apply {
            assertEquals(2f, x, 0.1f)
            assertEquals(4f, y, 0.1f)
            assertEquals(6f, z, 0.1f)
            assertEquals(1f, w, 0.1f)
        }
    }

    @Test
    fun transpose() {
        memory.space(1,
                Vector(1f, 5f, 9f, 13f),
                Vector(2f, 6f, 10f, 14f),
                Vector(3f, 7f, 11f, 15f),
                Vector(4f, 8f, 12f, 16f))

        memory.transpose(1)

        memory.multiply(Vector(1f, 2f, 3f, 4f), 1).apply {
            assertEquals(90f, x, 0.1f)
            assertEquals(100f, y, 0.1f)
            assertEquals(110f, z, 0.1f)
            assertEquals(120f, w, 0.1f)
        }
    }

    @Test
    fun perspective2D() {
        memory.perspective2D(0, 5f, 10f)

        memory.multiply(Vector(2f, 3f, -10f, 1f), 0).apply {
            perspectiveDivide()
            assertEquals(1f, z, 0.1f)
            assertEquals(2f / 10f, x, 0.1f)
            assertEquals(3f / 10f, y, 0.1f)
        }

        memory.multiply(Vector(2f, 3f, -5f, 1f), 0).apply {
            perspectiveDivide()
            assertEquals(0f, z, 0.1f)
            assertEquals(2f / 5f, x, 0.1f)
            assertEquals(3f / 5f, y, 0.1f)
        }

        memory.multiply(Vector(2f, 3f, -7f, 1f), 0).apply {
            perspectiveDivide()
            assertTrue(0f < z && z < 1f)
            assertEquals(2f / 7f, x, 0.1f)
            assertEquals(3f / 7f, y, 0.1f)
        }

        memory.multiply(Vector(2f, 3f, -2f, 1f), 0).apply {
            perspectiveDivide()
            Assert.assertTrue(z < 0f)
            assertEquals(2f / 2f, x, 0.1f)
            assertEquals(3f / 2f, y, 0.1f)
        }
    }

    @Test
    fun lookAt() {
        memory.lookAt(0,
                Vector(2f, 2f, 2f, 1f),
                Vector(0f, 0f, 0f, 1f),
                Vector(0f, 1f, 0f, 0f)
        )

        // Check that all matrix axis are perpendicular to each other:
        assertEquals(0f, memory[0, MatrixBuffer.FORWARD_ROW] * memory[0, MatrixBuffer.RIGHT_ROW], 0.1f)
        assertEquals(0f, memory[0, MatrixBuffer.RIGHT_ROW] * memory[0, MatrixBuffer.UP_ROW], 0.1f)
        assertEquals(0f, memory[0, MatrixBuffer.UP_ROW] * memory[0, MatrixBuffer.FORWARD_ROW], 0.1f)

        // Check the all matrix axis are unit vectors:
        assertEquals(1f, memory[0, MatrixBuffer.FORWARD_ROW].length, 0.1f)
        assertEquals(1f, memory[0, MatrixBuffer.RIGHT_ROW].length, 0.1f)
        assertEquals(1f, memory[0, MatrixBuffer.UP_ROW].length, 0.1f)

        memory.multiply(Vector(0f, 0f, 0f, 1f), 0).apply {
            assertEquals(0f, x, 0.1f)
            assertEquals(0f, y, 0.1f)
            assertTrue(z < 0f)
        }
    }

}
