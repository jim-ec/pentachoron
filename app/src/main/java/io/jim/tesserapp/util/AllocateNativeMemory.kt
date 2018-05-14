package io.jim.tesserapp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Returns natively allocated memory, considering the native byte endianness.
 * @param bytes Size of memory to be allocated, in bytes.
 */
fun allocateNativeMemory(bytes: Int) =
        ByteBuffer.allocateDirect(bytes)?.apply {
            order(ByteOrder.nativeOrder())
        }
                ?: throw RuntimeException("Cannot allocate native memory")

/**
 * Returns natively allocated memory, considering the native byte endianness.
 * @param floats Size of memory to be allocated, in floats.
 */
fun allocateNativeFloatMemory(floats: Int) =
        allocateNativeMemory(floats * Float.BYTE_LENGTH)
