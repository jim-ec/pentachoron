package io.jim.tesserapp.util

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 */
class InputStreamBuffer<T : Buffer.Element>(
        capacityGranularity: Int,
        elementSize: Int
) : Buffer<T>(capacityGranularity, elementSize) {

    /**
     * Append [element] to the buffer.
     */
    operator fun plusAssign(element: T) {
        if (element.floats.size != elementSize)
            throw InvalidElementException(element.floats.size, elementSize)

        while (floatBuffer.capacity() <= lastActiveElementIndex * elementSize) {
            increaseMemory()
        }

        for (i in 0 until elementSize) {
            floatBuffer.put(lastActiveElementIndex * elementSize + i, element.floats[i])
        }

        lastActiveElementIndex++
    }

}
