package io.jim.tesserapp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 */
abstract class Buffer(

        /**
         * As soon as the buffers needs more memory to store the elements,
         * the buffer gets re-allocated, gaining space for this count of elements.
         *
         * Therefore, this is the initial element capacity as well.
         */
        private val capacityGranularity: Int,

        /**
         * The size of each element.
         */
        val elementSize: Int
) {

    /**
     * Capacity of this buffer, expressed in elements.
     */
    var capacity = 0
        private set

    private lateinit var byteBuffer: ByteBuffer

    /**
     * Underlying float buffer.
     */
    lateinit var floatBuffer: FloatBuffer

    init {
        increaseMemory()
    }

    /**
     * Thrown when you try to access an invalid index.
     */
    class InvalidElementIndexException(elementIndex: Int)
        : RuntimeException("Invalid buffer element index $elementIndex")

    /**
     * Thrown when you try to access an invalid index.
     */
    class InvalidSubIndexException(subIndex: Int)
        : RuntimeException("Invalid buffer sub-index $subIndex")

    /**
     * Thrown when you try to write an element with a different list size than [elementSize].
     */
    inner class InvalidElementException(wrongFloatCount: Int)
        : RuntimeException("Invalid element: $wrongFloatCount floats given instead of $elementSize")

    /**
     * Increase the memory by [capacityGranularity] elements.
     */
    protected fun increaseMemory() {
        capacity += capacityGranularity

        // Allocate buffer:
        val newByteBuffer = ByteBuffer.allocateDirect(
                capacity * elementSize * Float.BYTE_LENGTH
        ).order(ByteOrder.nativeOrder())

        if (::byteBuffer.isInitialized) {
            // If old buffers hold data, copy them into the new buffer:
            newByteBuffer.put(byteBuffer)
            newByteBuffer.position(0)
        }

        byteBuffer = newByteBuffer
        floatBuffer = byteBuffer.asFloatBuffer()
    }

    /**
     * Return a single float of an element.
     * @param elementIndex Index of element to be queried.
     * @param subIndex Index of float to be returned within that element.
     */
    operator fun get(elementIndex: Int, subIndex: Int): Float {
        if (elementIndex < 0 || elementIndex >= capacity)
            throw InvalidElementIndexException(elementIndex)

        if (subIndex < 0 || subIndex >= elementSize)
            throw InvalidSubIndexException(subIndex)

        return floatBuffer[elementIndex * elementSize + subIndex]
    }

    /**
     * Writes [value] to the float at [subIndex] of element at [elementIndex].
     *
     * Assumes that such a location exists. Sub-classes take care of eventual re-allocations.
     * Otherwise, either [InvalidElementIndexException] or [InvalidSubIndexException] are thrown.
     */
    protected open operator fun set(elementIndex: Int, subIndex: Int, value: Float) {
        if (elementIndex < 0 || elementIndex >= capacity)
            throw InvalidElementIndexException(elementIndex)

        if (subIndex < 0 || subIndex >= elementSize)
            throw InvalidSubIndexException(subIndex)

        floatBuffer.put(elementIndex * elementSize + subIndex, value)
    }

}

/**
 * Byte length of one float.
 */
val Float.Companion.BYTE_LENGTH: Int
    get() = 4
