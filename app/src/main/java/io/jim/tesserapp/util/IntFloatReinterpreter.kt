package io.jim.tesserapp.util

import java.nio.ByteBuffer

/**
 * Convert integers to floating-points by keeping them byte-wise equally.
 * With this way, we can pass integer data into a float-memory.
 */
class IntFloatReinterpreter {

    private val byteBuffer = ByteBuffer.allocate(4)
    private val floatBuffer = byteBuffer.asFloatBuffer()
    private val intBuffer = byteBuffer.asIntBuffer()

    /**
     * Converts [int] into the corresponding floating point with the same memory representation.
     * Only `0` maps again to `0.0`, since both memory representations are equal.
     */
    fun toFloat(int: Int): Float {
        intBuffer.put(0, int)
        return floatBuffer.get(0)
    }

}
