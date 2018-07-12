package io.jim.tesserapp.graphics

import java.nio.FloatBuffer
import java.nio.IntBuffer

/**
 * Buffer providing int memory for OpenGL out-params.
 */
val resultCode = IntBuffer.allocate(10)
        ?: throw RuntimeException("Cannot allocate result code")

/**
 * Return currently stored result code.
 */
fun resultCode() = resultCode[0]

/**
 * Execute [f], returning result code after-wards.
 */
inline fun resultCode(f: () -> Unit): Int {
    f()
    return resultCode()
}

/**
 * Buffer providing float memory for OpenGL out-params.
 */
val floatResultCode = FloatBuffer.allocate(10)
        ?: throw RuntimeException("Cannot allocate float result code")

/**
 * Return currently stored result code.
 */
fun floatResultCode() = floatResultCode[0]

/**
 * Execute [f], returning result code after-wards.
 */
@Suppress("unused")
inline fun floatResultCode(crossinline f: () -> Unit): Float {
    f()
    return floatResultCode()
}
