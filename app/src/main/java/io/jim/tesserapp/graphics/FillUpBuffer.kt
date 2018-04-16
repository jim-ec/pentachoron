package io.jim.tesserapp.graphics

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Buffer which is filled with data.
 *
 * Data entries are structured with layouts, rather than being raw floats.
 */
data class FillUpBuffer(

        /**
         * Maximum capacity of entries.
         */
        val capacity: Int,

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
        val size = ranges.sum()

        /**
         * Total size of one entry according to this layout, in bytes.
         */
        val byteLength = size * FLOAT_BYTE_LENGTH

        /**
         * Ranges, expressed in bytes.
         */
        val byteRanges = ranges.map { it * FLOAT_BYTE_LENGTH }
    }

    private val byteBuffer = ByteBuffer.allocateDirect(capacity * layout.size * FLOAT_BYTE_LENGTH).apply {
        order(ByteOrder.nativeOrder())
    }

    internal val floatBuffer = byteBuffer.asFloatBuffer().apply {
        clear()
        while (position() < capacity()) put(0f)
        rewind()
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
    val byteCapacity = capacity * layout.size * FLOAT_BYTE_LENGTH

    /**
     * Get a single float from an [entry].
     * @param subIndex Denotes a single float within that entry.
     */
    operator fun get(entry: Int, subIndex: Int = 0) = floatBuffer[entry * layout.size + subIndex]

    /**
     * Thrown when data is given which does not match the buffer layout.
     */
    inner class InvalidEntryLayout(wrongLength: Int)
        : Exception("Invalid entry added: $wrongLength floats given, but ${layout.size} floats needed")

    /**
     * Thrown upon buffer overflow.
     */
    inner class OverflowException
        : Exception("Buffer overflow, at most $capacity can be stored")

    /**
     * Appends data to the buffer.
     *
     * The count of floats must match with the total size of the layout given earlier.
     *
     * @throws InvalidEntryLayout If data length does not match layout size.
     * @throws OverflowException If no more data can be added.
     */
    operator fun plusAssign(data: List<Float>) {
        if (layout.size != data.size)
            throw InvalidEntryLayout(data.size)

        if (floatBuffer.position() + layout.size > floatBuffer.capacity())
            throw OverflowException()

        if (floatBuffer.position() == 0) {
            activeEntries = 0
        }

        data.forEach {
            floatBuffer.put(it)
        }
        activeEntries++
    }

    /**
     * Prepare the buffer for new data.
     *
     * This discards previously hold data.
     */
    fun rewind() {
        floatBuffer.rewind()
    }

}
