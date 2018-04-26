package io.jim.tesserapp.util

import kotlin.math.max

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 */
class RandomAccessBuffer<T : Buffer.Element>(
        capacityGranularity: Int,
        elementSize: Int
) : Buffer<T>(capacityGranularity, elementSize) {

    /**
     * Set the [elementIndex]th element to [element].
     */
    operator fun set(elementIndex: Int, element: T) {
        if (element.floats.size != elementSize)
            throw InvalidElementException(element.floats.size, elementSize)

        while (floatBuffer.capacity() <= elementIndex * elementSize) {
            increaseMemory()
        }

        for (i in 0 until elementSize) {
            floatBuffer.put(elementIndex * elementSize + i, element.floats[i])
        }

        lastActiveElementIndex = max(lastActiveElementIndex, elementIndex)
    }

}
