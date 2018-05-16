package io.jim.tesserapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class InputStreamMemoryTest {

    private val memory = InputStreamMemory(allocationGranularity = 2, vectorsPerElement = 2)

    @Test
    fun initialCapacity() {
        assertEquals(2, memory.elementCapacity)
        assertEquals(0f, memory[0, 0, 0], 0.1f)
        assertEquals(0f, memory[0, 0, 1], 0.1f)
        assertEquals(0f, memory[1, 0, 0], 0.1f)
        assertEquals(0f, memory[1, 0, 1], 0.1f)
    }

    @Test(expected = RuntimeException::class)
    fun invalidElementIndex() {
        memory[3, 0, 0]
    }

    @Test(expected = RuntimeException::class)
    fun invalidVectorIndex() {
        memory[0, 8, 0]
    }

    @Test(expected = RuntimeException::class)
    fun invalidFloatIndex() {
        memory[0, 0, 8]
    }

    @Test(expected = RuntimeException::class)
    fun invalidElement() {
        memory.record {
            memory.write(3f, 4f, 5f, 1f)
        }
    }

    @Test
    fun writeRead() {
        memory.record {
            memory.write(8f, 9f, 0f, 0f)
            memory.write(0f, 0f, 0f, 0f)

            memory.write(1f, 2f, 0f, 0f)
            memory.write(0f, 0f, 0f, 0f)
        }

        assertEquals(1f, memory[1, 0, 0], 0.1f)
        assertEquals(2f, memory[1, 0, 1], 0.1f)
    }

    @Test
    fun increaseMemoryAndPreserveOldData() {

        memory.record {
            memory.write(4f, 5f, 0f, 0f)
            memory.write(0f, 0f, 0f, 0f)

            memory.write(7f, 8f, 0f, 0f)
            memory.write(0f, 0f, 0f, 0f)

            memory.write(1f, 2f, 0f, 0f)
            memory.write(0f, 0f, 0f, 0f)
        }

        assertEquals(4, memory.elementCapacity)

        assertEquals(4f, memory[0, 0, 0], 0.1f)
        assertEquals(5f, memory[0, 0, 1], 0.1f)

        assertEquals(7f, memory[1, 0, 0], 0.1f)
        assertEquals(8f, memory[1, 0, 1], 0.1f)

        assertEquals(1f, memory[2, 0, 0], 0.1f)
        assertEquals(2f, memory[2, 0, 1], 0.1f)
    }

}
