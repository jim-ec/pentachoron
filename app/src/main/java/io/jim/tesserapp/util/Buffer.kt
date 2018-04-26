package io.jim.tesserapp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 *
 * TODO: Implement InputStreamBuffer and RandomAccessBuffer
 */
abstract class Buffer<T : Buffer.Element>(

        /**
         * Initial memory size.
         * As soon as buffers needs to resize, this count of elements is added.
         */
        private val capacityGranularity: Int,

        /**
         * The size of each element.
         */
        protected val elementSize: Int
) {

    /**
     * Buffer elements, over which the buffer iterates when copying them into memory.
     */
    interface Element {
        /**
         * Floats to be stored into the buffer.
         */
        val floats: List<Float>
    }

    /**
     * Capacity of this buffer, expressed in elements.
     */
    var capacity = 0
        private set

    /**
     * Greatest index a value was written to since the last call to [rewind] or construction time.
     */
    var lastActiveElementIndex = 0
        protected set

    private lateinit var byteBuffer: ByteBuffer
    internal lateinit var floatBuffer: FloatBuffer

    init {
        increaseMemory()
    }

    /**
     * Thrown when you try to write-in an element with a different list size than [elementSize].
     */
    class InvalidElementException(wrongFloatCount: Int, neededFloatCount: Int)
        : RuntimeException("Invalid element: $wrongFloatCount floats given instead of $neededFloatCount")

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
     * Reset [lastActiveElementIndex] to 0.
     */
    fun rewind() {
        lastActiveElementIndex = 0
    }

}

/**
 * Byte length of one float.
 */
val Float.Companion.BYTE_LENGTH: Int
    get() = 4
