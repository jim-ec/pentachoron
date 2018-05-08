package io.jim.tesserapp.util

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 */
class InputStreamBuffer(
        capacityGranularity: Int,
        elementSize: Int
) : Buffer(capacityGranularity, elementSize) {

    /**
     * The count of elements written to this buffer since the last call to [rewind] or
     * construction time.
     */
    val writtenElementCounts
        get() = lastActiveElementIndex + 1

    /**
     * Greatest index a value was written to since the last call to [rewind] or construction time.
     */
    private var lastActiveElementIndex = 0

    /**
     * Append [floats] to the buffer.
     * @throws Buffer.InvalidElementException If the count of floats in the list do not match up with [elementSize].
     */
    operator fun plusAssign(floats: List<Float>) {
        if (floats.size != elementSize)
            throw InvalidElementException(floats.size)

        if (floatBuffer.capacity() <= lastActiveElementIndex * elementSize) {
            // Re-allocate memory.
            // Since the granularity must be at least 1, at most one re-allocation is necessary
            // per one call to plusAssign().
            increaseMemory()
        }

        for (i in 0 until elementSize) {
            floatBuffer.put(lastActiveElementIndex * elementSize + i, floats[i])
        }

        lastActiveElementIndex++
    }

    /**
     * Reset [lastActiveElementIndex] to 0.
     */
    fun rewind() {
        lastActiveElementIndex = 0
    }

}
