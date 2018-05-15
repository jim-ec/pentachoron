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
     * The count of elements written to this buffer since the last call to [finalize] or
     * construction time.
     */
    val writtenElementCounts: Int
        get() = writtenVectorCounts / vectorsPerElement

    var writtenVectorCounts = 0
        private set

    /**
     * Write a single vector into the buffer.
     * If necessary, memory is increased by [allocationGranularity] elements.
     *
     * No check is done to ensure that this writes a proper number of vectors into the buffer.
     * If used directly, this can lead to excess vectors if their count does not match up with
     * vectors taken by a single element.
     *
     * To write vectors into the buffer in a safer manner, meaning that excess vectors lead to
     * an exception, use [record] as a block function.
     *
     * @see record
     */
    fun write(x: Float, y: Float, z: Float, q: Float) {

        // Check if memory must be extended:
        if (writtenElementCounts * floatsPerElement >= floatBuffer.capacity()) {
            increaseMemory()
        }

        floatBuffer.put(writtenVectorCounts * 4 + 0, x)
        floatBuffer.put(writtenVectorCounts * 4 + 1, y)
        floatBuffer.put(writtenVectorCounts * 4 + 2, z)
        floatBuffer.put(writtenVectorCounts * 4 + 3, q)

        writtenVectorCounts++
    }

    /**
     * Invoke [f]. Count of written vectors after the call is compared to the count of vectors
     * present before the call. If that count does not match up with vectors taken by `n` element,
     * determined through [vectorsPerElement], an exception is thrown.
     *
     * So this is a safer way to write vectors into the buffer, instead of using [write] without
     * this block function.
     *
     * @throws ExcessVectorRecordedException If recorded vector counts does not match up with
     * vectors consumed per element.
     */
    inline fun record(f: (buffer: InputStreamBuffer) -> Unit) {
        val oldVectorCounts = writtenVectorCounts

        f(this)

        val excessVectors = (writtenVectorCounts - oldVectorCounts) % vectorsPerElement
        if (excessVectors > 0)
            throw ExcessVectorRecordedException(excessVectors)
    }

    /**
     * Reset [writtenElementCounts] to 0.
     * Next data write will start from buffer begin.
     */
    fun finalize() {
        writtenVectorCounts = 0
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
     * @param vectorIndex Index of vector within that element.
     * @param floatIndex Index of float within that vector.
     */
    operator fun get(elementIndex: Int, vectorIndex: Int, floatIndex: Int): Float {
        if (elementIndex < 0 || elementIndex >= elementCapacity)
            throw RuntimeException("Invalid element index $elementIndex")

        if (vectorIndex < 0 || vectorIndex >= vectorsPerElement)
            throw RuntimeException("Invalid vector index $vectorIndex")

        if (floatIndex < 0 || floatIndex >= 4)
            throw RuntimeException("Invalid float index $floatIndex")

        return floatBuffer[elementIndex * floatsPerElement +
                vectorIndex * 4 +
                floatIndex]
    }

    /**
     * Thrown when you try to access an invalid index.
     */
    inner class ExcessVectorRecordedException(excessVectorCounts: Int)
        : RuntimeException("Recording $excessVectorCounts excess vectors, " +
            "$vectorsPerElement vectors consumed per element")

}
