package io.jim.tesserapp.gl

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
