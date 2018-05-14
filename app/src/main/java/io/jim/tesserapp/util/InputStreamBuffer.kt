package io.jim.tesserapp.util

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 */
class InputStreamBuffer(
        /**
         * As soon as the buffers needs more memory to store the elements,
         * the buffer gets re-allocated, gaining space for this count of elements.
         *
         * Therefore, this is the initial element capacity as well.
         */
        private val allocationGranularity: Int,

        /**
         * The size of each element.
         */
        val floatsPerElement: Int

) {

    /**
     * Underlying byte buffer.
     */
    private var byteBuffer = allocateNativeFloatMemory(allocationGranularity * floatsPerElement)

    /**
     * Underlying float buffer.
     */
    var floatBuffer = byteBuffer.asFloatBuffer()
            ?: throw RuntimeException("Cannot create float buffer view onto byte buffer")

    /**
     * Capacity of this buffer, expressed in elements.
     */
    var capacity = allocationGranularity
        private set

    /**
     * The count of elements written to this buffer since the last call to [rewind] or
     * construction time.
     */
    var writtenElementCounts = 0
        private set

    /**
     * Append [floats] to the buffer.
     * @throws InvalidElementException If the count of floats in the list do not match up with [floatsPerElement].
     */
    operator fun plusAssign(floats: List<Float>) {
        if (floats.size != floatsPerElement)
            throw InvalidElementException(floats.size)

        if (writtenElementCounts * floatsPerElement >= floatBuffer.capacity()) {
            // Re-allocate memory.
            // Since the granularity must be at least 1, at most one re-allocation is necessary
            // per one call to plusAssign().
            increaseMemory()
        }

        for (i in 0 until floatsPerElement) {
            floatBuffer.put(writtenElementCounts * floatsPerElement + i, floats[i])
        }

        writtenElementCounts++
    }

    /**
     * Reset [writtenElementCounts] to 0.
     * Next data write will start from buffer begin.
     */
    fun rewind() {
        writtenElementCounts = 0
    }

    /**
     * Increase the memory by [allocationGranularity] elements.
     */
    private fun increaseMemory() {
        capacity += allocationGranularity

        // Allocate buffer:
        val newByteBuffer = allocateNativeFloatMemory(capacity * floatsPerElement)

        // Copy contents of old buffer into new buffer:
        newByteBuffer.put(byteBuffer)
        newByteBuffer.position(0)

        byteBuffer = newByteBuffer
        floatBuffer = byteBuffer.asFloatBuffer()
                ?: throw RuntimeException("Cannot create float buffer view onto byte buffer")
    }

    /**
     * Return a single float of an element.
     * @param elementIndex Index of element to be queried.
     * @param subIndex Index of float to be returned within that element.
     */
    operator fun get(elementIndex: Int, subIndex: Int): Float {
        if (elementIndex < 0 || elementIndex >= capacity)
            throw InvalidElementIndexException(elementIndex)

        if (subIndex < 0 || subIndex >= floatsPerElement)
            throw InvalidSubIndexException(subIndex)

        return floatBuffer[elementIndex * floatsPerElement + subIndex]
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
     * Thrown when you try to write an element with a different list size than [floatsPerElement].
     */
    inner class InvalidElementException(wrongFloatCount: Int)
        : RuntimeException("Invalid element: $wrongFloatCount floats given instead of $floatsPerElement")

}
