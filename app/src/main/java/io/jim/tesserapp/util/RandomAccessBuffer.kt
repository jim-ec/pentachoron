package io.jim.tesserapp.util

/**
 * A buffer, capable of resizing.
 * Contents are kept in elements, equally sized memory sections.
 */
class RandomAccessBuffer(
        capacityGranularity: Int,
        elementSize: Int
) : Buffer(capacityGranularity, elementSize) {

    /**
     * Writes [value] to the float at [subIndex] of element at [elementIndex].
     * Memory is increased if [elementIndex] would exceed the current [capacity].
     *
     * @throws InvalidSubIndexException If [subIndex] does not identify a valid float within the element.
     */
    public override operator fun set(elementIndex: Int, subIndex: Int, value: Float) {
        while (floatBuffer.capacity() <= elementIndex * elementSize) {
            // Increase memory as long as the element index is out of bounds:
            increaseMemory()
        }

        // Actually write the float:
        super.set(elementIndex, subIndex, value)
    }

    /**
     * Write a complete element to [elementIndex].
     * @param floats Float values to be written. Represents one element.
     * @throws InvalidElementException If float value count differs from [elementSize].
     */
    operator fun set(elementIndex: Int, floats: List<Float>) {
        if (floats.size != elementSize)
            throw InvalidElementException(floats.size)

        for (subIndex in 0 until elementSize) {
            this[elementIndex, subIndex] = floats[subIndex]
        }
    }

}
