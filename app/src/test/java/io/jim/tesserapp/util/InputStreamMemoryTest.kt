package io.jim.tesserapp.util

import io.jim.tesserapp.cpp.InputStreamMemory
import org.junit.Assert.assertEquals
import org.junit.Test

class InputStreamMemoryTest {
    
    private val memory = InputStreamMemory(allocationGranularity = 2, vectorsPerElement = 2)
    
    @Test
    fun initialCapacity() {
        assertEquals(2, memory.elementCapacity)
        assertEquals(0.0, memory[0, 0, 0], 0.1)
        assertEquals(0.0, memory[0, 0, 1], 0.1)
        assertEquals(0.0, memory[1, 0, 0], 0.1)
        assertEquals(0.0, memory[1, 0, 1], 0.1)
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
            memory.write(3.0, 4.0, 5.0, 1.0)
        }
    }
    
    @Test
    fun writeRead() {
        memory.record {
            memory.write(8.0, 9.0, 0.0, 0.0)
            memory.write(0.0, 0.0, 0.0, 0.0)
    
            memory.write(1.0, 2.0, 0.0, 0.0)
            memory.write(0.0, 0.0, 0.0, 0.0)
        }
        
        assertEquals(1.0, memory[1, 0, 0], 0.1)
        assertEquals(2.0, memory[1, 0, 1], 0.1)
    }
    
    @Test
    fun increaseMemoryAndPreserveOldData() {
        
        memory.record {
            memory.write(4.0, 5.0, 0.0, 0.0)
            memory.write(0.0, 0.0, 0.0, 0.0)
            
            memory.write(7.0, 8.0, 0.0, 0.0)
            memory.write(0.0, 0.0, 0.0, 0.0)
            
            memory.write(1.0, 2.0, 0.0, 0.0)
            memory.write(0.0, 0.0, 0.0, 0.0)
        }
        
        assertEquals(4, memory.elementCapacity)
        
        assertEquals(4.0, memory[0, 0, 0], 0.1)
        assertEquals(5.0, memory[0, 0, 1], 0.1)
        
        assertEquals(7.0, memory[1, 0, 0], 0.1)
        assertEquals(8.0, memory[1, 0, 1], 0.1)
        
        assertEquals(1.0, memory[2, 0, 0], 0.1)
        assertEquals(2.0, memory[2, 0, 1], 0.1)
    }
    
}
