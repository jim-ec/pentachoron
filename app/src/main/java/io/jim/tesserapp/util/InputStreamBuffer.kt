package io.jim.tesserapp.util

/**
 * A buffer aligned to 4-float sized vectors, capable of resizing.
 *
 * Contents are kept in elements, equally sized memory sections.
 * Each element consist of a defined number of 4-float vectors.
 *
 * Consider the nomenclature:
 * - A buffer consists of one or more **elements**.
 * - Each element consists of one or more **vector**.
 * - Each vector consists of exactly **4 floats**, due to alignment.
 */
class InputStreamBuffer(

        /**
         * Size of memory units this buffer allocates when resizing, expressed in elements.
         */
        private val allocationGranularity: Int,

        /**
         * Counts of vectors each element consists of.
         */
        val vectorsPerElement: Int
) {

    private val floatsPerElement = vectorsPerElement * 4

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
    val elementCapacity: Int
        get() = floatBuffer.capacity() / floatsPerElement

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

        // Check if memory must be extended:
        if (writtenElementCounts * floatsPerElement >= floatBuffer.capacity()) {
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

        // Allocate buffer:
        val newByteBuffer = allocateNativeFloatMemory(floatBuffer.capacity()
                + allocationGranularity * floatsPerElement)

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
        if (elementIndex < 0 || elementIndex >= elementCapacity)
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
