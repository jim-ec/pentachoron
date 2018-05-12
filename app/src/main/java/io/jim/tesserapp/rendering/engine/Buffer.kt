package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import io.jim.tesserapp.rendering.resultCode
import java.nio.ByteBuffer

class Buffer(
        val target: Int,
        val floatCapacity: Int,
        initialData: java.nio.Buffer?,
        usage: Int = GLES30.GL_STATIC_DRAW
) {

    val handle = resultCode { GLES30.glGenBuffers(1, resultCode) }

    companion object {
        const val FLOAT_BYTE_LENGTH = 4
    }

    init {
        GLES30.glBufferData(target, floatCapacity * FLOAT_BYTE_LENGTH, initialData, usage)
    }

    fun write(data: java.nio.Buffer, size: Int = floatCapacity, offset: Int = 0) {
        GLES30.glBufferSubData(target, offset, size * FLOAT_BYTE_LENGTH, data)
    }

    inline fun bound(f: () -> Unit) {
        GLES30.glBindBuffer(target, handle)
        f()
        GLES30.glBindBuffer(target, 0)
    }

    inline fun mapped(access: Int = GLES30.GL_MAP_READ_BIT, f: (buffer: java.nio.FloatBuffer) -> Unit) {
        val mappedBuffer = GLES30.glMapBufferRange(
                target,
                0,
                floatCapacity,
                access
        )

        f((mappedBuffer as ByteBuffer).asFloatBuffer())

        GLES30.glUnmapBuffer(target)
    }

}
