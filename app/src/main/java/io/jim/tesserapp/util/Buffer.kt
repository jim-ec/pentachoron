package io.jim.tesserapp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 */
class Buffer<T : Buffer.Element>(

        /**
         * Initial memory size.
         * As soon as buffers needs to resize, this count of elements is added.
         */
        private val capacityGranularity: Int,

        /**
         * The size of each element.
         */
        private val elementSize: Int
) {

    /**
     * Buffer elements are in fact lists, over which the buffer iterates when copying them into
     * its memory.
     */
    interface Element : List<Float>

    private var capacity = 0
    private lateinit var byteBuffer: ByteBuffer
    private lateinit var floatBuffer: FloatBuffer

    init {
        increaseMemory()
    }

    /**
     * Thrown when you try to write-in an element with a different list size than [elementSize].
     */
    class InvalidElementException(wrongFloatCount: Int, neededFloatCount: Int)
        : RuntimeException("Invalid element: $wrongFloatCount floats given instead of $neededFloatCount")

    /**
     * Increase the memory by [capacityGranularity] elements.
     */
    private fun increaseMemory() {
        capacity += capacityGranularity

        // Allocate buffer:
        val newByteBuffer = ByteBuffer.allocateDirect(
                capacity * elementSize * FLOAT_BYTE_LENGTH
        ).order(ByteOrder.nativeOrder())

        // Copy contents from old byte-buffer: TODO: use buffer bulk method, then reset position
        if (::byteBuffer.isInitialized) {
            for (i in 0 until byteBuffer.capacity()) {
                newByteBuffer.put(i, byteBuffer[i])
            }
        }

        byteBuffer = newByteBuffer
        floatBuffer = byteBuffer.asFloatBuffer()
    }

    /**
     * Set the [elementIndex]th element to [element].
     */
    operator fun set(elementIndex: Int, element: T) {
        if (element.size != elementSize)
            throw InvalidElementException(element.size, elementSize)

        while (floatBuffer.capacity() <= elementIndex * elementSize) {
            increaseMemory()
        }

        for (i in 0 until elementSize) {
            floatBuffer.put(elementIndex * elementSize + i, element[i])
        }
    }

    //inline operator fun <reified T : Element> get(elementIndex: Int) = T()

    companion object {
        private const val FLOAT_BYTE_LENGTH = 4
    }

}
