package io.jim.tesserapp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Returns natively allocated memory, considering the native byte endianness.
 */
fun allocateNativeMemory(bytes: Int) =
        ByteBuffer.allocateDirect(bytes).apply {
            order(ByteOrder.nativeOrder())
        }
