package io.jim.tesserapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class RandomAccessBufferTest {

    private open class Entry(a: Float, b: Float) : Buffer.Element {
        override val floats = listOf(a, b)
    }

    private val buffer = RandomAccessBuffer<Entry>(2, 2)

    @Test
    fun initialCapacity() {
        assertEquals(2, buffer.capacity)
        assertEquals(0f, buffer[0, 0], 0.1f)
        assertEquals(0f, buffer[0, 1], 0.1f)
        assertEquals(0f, buffer[1, 0], 0.1f)
        assertEquals(0f, buffer[1, 1], 0.1f)
    }

    @Test(expected = Buffer.InvalidElementIndexException::class)
    fun invalidElementIndex() {
        buffer[3, 0]
    }

    @Test(expected = Buffer.InvalidSubIndexException::class)
    fun invalidSubIndex() {
        buffer[0, 3]
    }

    @Test(expected = Buffer.InvalidElementException::class)
    fun invalidElement() {
        class WrongEntry(a: Float, b: Float, c: Float) : Entry(a, b) {
            override val floats = listOf(a, b, c)
        }
        buffer[0] = WrongEntry(3f, 4f, 5f)
    }

    @Test
    fun writeRead() {
        buffer[1] = Entry(1f, 2f)
        assertEquals(1f, buffer[1, 0], 0.1f)
        assertEquals(2f, buffer[1, 1], 0.1f)
    }

    @Test
    fun increaseMemoryAndPreserveOldData() {
        buffer[0] = Entry(4f, 5f)
        buffer[1] = Entry(7f, 8f)
        buffer[2] = Entry(1f, 2f)

        assertEquals(4, buffer.capacity)

        assertEquals(4f, buffer[0, 0], 0.1f)
        assertEquals(5f, buffer[0, 1], 0.1f)

        assertEquals(7f, buffer[1, 0], 0.1f)
        assertEquals(8f, buffer[1, 1], 0.1f)

        assertEquals(1f, buffer[2, 0], 0.1f)
        assertEquals(2f, buffer[2, 1], 0.1f)
    }

}
