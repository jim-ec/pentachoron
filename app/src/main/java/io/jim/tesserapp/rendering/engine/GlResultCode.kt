package io.jim.tesserapp.rendering.engine

import java.nio.IntBuffer

/**
 * Buffer providing one int for OpenGL out-params.
 */
val resultCode = IntBuffer.allocate(1) ?: throw RuntimeException("Cannot allocate result code")

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
