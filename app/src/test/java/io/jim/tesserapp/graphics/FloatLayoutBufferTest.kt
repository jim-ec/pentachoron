package io.jim.tesserapp.graphics

import junit.framework.Assert.assertEquals
import org.junit.Test

class FloatLayoutBufferTest {

    private val ranges = listOf(1, 2, 3)
    private val layout = FloatLayoutBuffer.Layout(ranges)
    private val buffer = FloatLayoutBuffer<List<Float>>(10, layout)

    @Test
    fun layout() {
        assertEquals(6, layout.size)
        assertEquals(24, layout.byteLength)
        assertEquals(listOf(4, 8, 12), layout.byteRanges)
    }

    @Test
    fun capacity() {
        assertEquals(10, buffer.capacity)
        assertEquals(10 * 4 * 6, buffer.byteCapacity)
        assertEquals(0, buffer.activeEntries)
    }

    @Test
    fun rewindingDoesNotChangeCapacity() {
        buffer += listOf(1f, 2f, 2f, 3f, 3f, 3f)
        assertEquals(10, buffer.capacity)
        assertEquals(10 * 4 * 6, buffer.byteCapacity)
        buffer.rewind()
        assertEquals(10, buffer.capacity)
        assertEquals(10 * 4 * 6, buffer.byteCapacity)
    }

    @Test
    fun rewindingDoesNotResetActiveEntryCount() {
        buffer += listOf(1f, 2f, 2f, 3f, 3f, 3f)
        buffer += listOf(1f, 2f, 2f, 3f, 3f, 3f)
        buffer += listOf(1f, 2f, 2f, 3f, 3f, 3f)
        assertEquals(3, buffer.activeEntries)
        buffer.rewind()
        assertEquals(3, buffer.activeEntries)
        buffer += listOf(1f, 2f, 2f, 3f, 3f, 3f)
        assertEquals(1, buffer.activeEntries)
    }

    @Test(expected = FloatLayoutBuffer.InvalidEntryLayout::class)
    fun invalidEntryAdd() {
        buffer += listOf(1f, 5f, 6f)
    }

    @Test
    fun filling() {
        for (i in 0 until 10) {
            buffer += listOf(1f, 2f, 2f, 3f, 3f, 3f)
            assertEquals(i + 1, buffer.activeEntries)
        }
    }

    @Test(expected = FloatLayoutBuffer.OverflowException::class)
    fun overflow() {
        for (i in 0..10) {
            buffer += listOf(1f, 2f, 2f, 3f, 3f, 3f)
        }
    }

}
