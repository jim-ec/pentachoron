package io.jim.tesserapp.util

import io.jim.tesserapp.graphics.FLOAT_BYTE_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Returns natively allocated memory, considering the native byte endianness.
 * @param bytes Size of memory to be allocated, in bytes.
 */
fun allocateNativeMemory(bytes: Int) =
        ByteBuffer.allocateDirect(bytes)!!.apply {
            order(ByteOrder.nativeOrder())
        }

/**
 * Returns natively allocated memory, considering the native byte endianness.
 * The generated buffer is still a [ByteBuffer], only it's memory size is always a multiply
 * of a float's memory size.
 * @param floats Size of memory to be allocated, in floats.
 */
fun allocateNativeFloatMemory(floats: Int) =
        allocateNativeMemory(floats * FLOAT_BYTE_LENGTH)
