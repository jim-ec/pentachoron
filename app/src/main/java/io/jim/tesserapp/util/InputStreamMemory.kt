package io.jim.tesserapp.util

/**
 * Memory aligned to 4-float sized vectors.
 *
 * Memory is filled in a stream-like manner, i.e. there is no random access getter.
 * Instead, write operations always *append* content to existing memory.
 *
 * **In case of memory shortage, re-allocation will occur** and the memory will be expanded by
 * [allocationGranularity] elements.
 *
 * To rewrite data, i.e. starting from the very beginning and *discarding* previous memory contents,
 * you have to *rewind* the memory, using [rewind].
 *
 * Contents are kept in elements, equally sized memory sections.
 * Each element consists of 4-float vectors, whose exact counts is defined at construction time
 * using [vectorsPerElement].
 *
 * Nomenclature:
 * - Memory consists of one or more **elements**.
 * - Each element consists of one or more **vectors**.
 * - Each vector consists of exactly **4 floats**, due to alignment.
 */
class InputStreamMemory(

        /**
         * Count of elements the memory grows by when reallocating.
         */
        private val allocationGranularity: Int,

        /**
         * Counts of vectors each element consists of.
         */
        val vectorsPerElement: Int
) {

    private val floatsPerElement = vectorsPerElement * 4

    /**
     * Underlying byte memory.
     */
    private var byteMemory = allocateNativeFloatMemory(allocationGranularity * floatsPerElement)

    /**
     * Underlying float memory.
     */
    var floatMemory = byteMemory.asFloatBuffer()
            ?: throw RuntimeException("Cannot create float memory view onto byte memory")

    /**
     * Capacity of memory, expressed in elements.
     */
    val elementCapacity: Int
        get() = floatMemory.capacity() / floatsPerElement

    /**
     * The count of elements written into memory since the last call to [rewind] or
     * construction time.
     */
    val writtenElementCounts: Int
        get() = writtenVectorCounts / vectorsPerElement

    /**
     * Count of written vectors.
     * This number is reset by a call to [rewind].
     */
    var writtenVectorCounts = 0
        private set

    /**
     * Write a single vector into the memory.
     * If necessary, memory is increased by [allocationGranularity] elements.
     *
     * No check is done to ensure that this writes a proper number of vectors into the memory.
     * If used directly, this can lead to excess vectors if their count does not match up with
     * vectors taken by a single element.
     *
     * To write vectors into memory in a safer manner, meaning that excess vectors lead to
     * an exception, use [record] as a block function.
     *
     * @see record
     */
    fun write(x: Float, y: Float, z: Float, q: Float) {

        // Check if memory must be extended:
        if (writtenElementCounts * floatsPerElement >= floatMemory.capacity()) {
            increaseMemory()
        }

        // Put the whole given vector into the float memory:
        floatMemory.put(writtenVectorCounts * 4 + 0, x)
        floatMemory.put(writtenVectorCounts * 4 + 1, y)
        floatMemory.put(writtenVectorCounts * 4 + 2, z)
        floatMemory.put(writtenVectorCounts * 4 + 3, q)

        // Increment counts of written vectors:
        writtenVectorCounts++
    }

    /**
     * Invoke [f]. Count of written vectors after the call is compared to the count of vectors
     * present before the call. If that count does not match up with vectors taken by `n` element,
     * determined through [vectorsPerElement], an exception is thrown.
     *
     * So this is a safer way to write vectors into memory, instead of using [write] without
     * this block function.
     *
     * @throws ExcessVectorRecordedException If recorded vector counts does not match up with
     * vectors consumed per element.
     */
    inline fun record(f: (memory: InputStreamMemory) -> Unit) {

        // Remember count of written vectors before executing lambda:
        val oldVectorCounts = writtenVectorCounts

        f(this)

        // Compute counts of excessively written vectors:
        val excessVectors = (writtenVectorCounts - oldVectorCounts) % vectorsPerElement
        if (excessVectors > 0)
            throw ExcessVectorRecordedException(excessVectors)
    }

    /**
     * Following write operations will overwrite existing content.
     * Though this will reset the counts of written vectors to zero,
     * this operation does not delete or invalid any memory, i.e. you can use stored data as is,
     * unless you write new data into the memory.
     */
    fun rewind() {
        writtenVectorCounts = 0
    }

    /**
     * Increase the memory by [allocationGranularity] elements.
     */
    private fun increaseMemory() {

        // Allocate memory:
        val newByteMemory = allocateNativeFloatMemory(floatMemory.capacity()
                + allocationGranularity * floatsPerElement)

        // Copy contents of old memory into new memory:
        newByteMemory.put(byteMemory)
        newByteMemory.position(0)

        byteMemory = newByteMemory
        floatMemory = byteMemory.asFloatBuffer()
                ?: throw RuntimeException("Cannot create float memory view onto byte memory")
    }

    /**
     * Return a single float of an element.
     *
     * @param elementIndex Index of element to be queried.
     * @param vectorIndex Index of vector within that element.
     * @param floatIndex Index of float within that vector.
     *
     * @throws RuntimeException
     * If [elementIndex] is either negative or references an element
     * outside the current memory capacity.
     *
     * @throws RuntimeException
     * If [vectorIndex] is either negative or references a vector
     * outside the count of vectors hold by a single element.
     *
     * @throws RuntimeException
     * If [floatIndex] is outside the range `[0,3]`.
     *
     */
    operator fun get(elementIndex: Int, vectorIndex: Int, floatIndex: Int): Float {
        if (elementIndex < 0 || elementIndex >= elementCapacity)
            throw RuntimeException("Invalid element index $elementIndex")

        if (vectorIndex < 0 || vectorIndex >= vectorsPerElement)
            throw RuntimeException("Invalid vector index $vectorIndex")

        if (floatIndex < 0 || floatIndex >= 4)
            throw RuntimeException("Invalid float index $floatIndex")

        return floatMemory[elementIndex * floatsPerElement +
                vectorIndex * 4 +
                floatIndex]
    }

    /**
     * Thrown when writing excess vectors.
     */
    inner class ExcessVectorRecordedException(excessVectorCounts: Int)
        : RuntimeException("Recording $excessVectorCounts excess vectors, " +
            "$vectorsPerElement vectors consumed per element")

}
