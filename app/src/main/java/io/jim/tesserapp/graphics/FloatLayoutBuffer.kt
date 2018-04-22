package io.jim.tesserapp.graphics

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

/**
 * Buffer which is filled with data.
 *
 * Data entries are structured with layouts, rather than being raw floats.
 *
 * The buffer itself is resizable.
 * As soon as you put more data in it via [plusAssign] when it can hold,
 * the buffer will resize by [initialCapacity] entries.
 */
class FloatLayoutBuffer<in T : Iterable<Float>>(

        /**
         * Initial maximum capacity of entries.
         * Buffer re-allocations will resize the buffer by this value.
         */
        private val initialCapacity: Int,

        /**
         * Memory layout of a single entry.
         */
        val layout: Layout

) {

    companion object {

        /**
         * Size of one float, in bytes.
         */
        const val FLOAT_BYTE_LENGTH = 4
    }

    /**
     * Each buffer entries is structured by such a layout.
     */
    data class Layout(
            /**
             * Ranges of the layout.
             */
            private val ranges: List<Int>
    ) {
        constructor(vararg ranges: Int) : this(ranges.toList())

        /**
         * Total size of one entry according to this layout.
         */
        val floatLength = ranges.sum()

        /**
         * Total size of one entry according to this layout, in bytes.
         */
        val byteLength = floatLength * FLOAT_BYTE_LENGTH

        /**
         * Ranges, expressed in bytes.
         */
        val byteRanges = ranges.map { it * FLOAT_BYTE_LENGTH }
    }

    private lateinit var byteBuffer: ByteBuffer
    internal lateinit var floatBuffer: FloatBuffer

    /**
     * Count of maximum entries.
     */
    var capacity = 0

    init {
        if (initialCapacity <= 0)
            throw RuntimeException("Initial size must be greater than 0")
        increaseMemory()
    }

    /**
     * Increase the internal memory size by the original [initialCapacity].
     * Buffer position of [floatBuffer] is preserved, if [floatBuffer] was already allocated.
     */
    private fun increaseMemory() {

        // Remember last position if float-buffer was previously initialized:
        val oldPosition = if (::floatBuffer.isInitialized) floatBuffer.position() else 0

        // Increase capacity, this value is used when allocating memory:
        capacity += initialCapacity

        // Allocate buffer:
        val newByteBuffer = ByteBuffer.allocateDirect(
                capacity * layout.floatLength * FLOAT_BYTE_LENGTH
        ).order(ByteOrder.nativeOrder())

        if (::byteBuffer.isInitialized) {
            // Copy content from old byte-buffer:
            for (i in 0 until byteBuffer.capacity()) {
                newByteBuffer.put(i, byteBuffer[i])
            }
        }

        byteBuffer = newByteBuffer

        floatBuffer = byteBuffer.asFloatBuffer()
        floatBuffer.position(oldPosition)
    }

    /**
     * Count of currently added entries.
     * The count is not reset by [rewind], but instead by the next call to [plusAssign] after
     * calling [rewind].
     */
    var activeEntries = 0
        private set

    /**
     * Total length of this buffer in bytes, regardless how much data has been recorded until now.
     */
    val byteCapacity = capacity * layout.floatLength * FLOAT_BYTE_LENGTH

    /**
     * Get a single float from an [entry].
     * @param subIndex Denotes a single float within that entry.
     */
    operator fun get(entry: Int, subIndex: Int = 0) = floatBuffer[entry * layout.floatLength + subIndex]

    /**
     * Thrown when data is given which does not match the buffer layout.
     */
    class InvalidEntryLayout(wrongLength: Int, neededLength: Int)
        : Exception("Invalid entry added: $wrongLength floats given, but $neededLength floats needed")

    /**
     * Appends data to the buffer.
     *
     * The count of floats must match with the total size of the layout given earlier.
     *
     * @throws InvalidEntryLayout If data length does not match layout size.
     */
    operator fun plusAssign(data: T) {

        if (floatBuffer.position() + layout.floatLength > floatBuffer.capacity()) {
            // Re-allocate buffer:
            increaseMemory()
        }

        if (floatBuffer.position() == 0) {
            activeEntries = 0
        }

        var dataCounter = 0
        data.forEach {
            floatBuffer.put(it)
            dataCounter++
        }
        if (layout.floatLength != dataCounter)
            throw InvalidEntryLayout(dataCounter, layout.floatLength)

        activeEntries++
    }

    /**
     * Prepare the buffer for new data.
     *
     * This discards previously hold data.
     *
     * @return Count of bytes this buffer hold before rewinding.
     */
    fun rewind(): Int {
        val pos = floatBuffer.position()
        floatBuffer.rewind()
        return pos * FLOAT_BYTE_LENGTH
    }

}
