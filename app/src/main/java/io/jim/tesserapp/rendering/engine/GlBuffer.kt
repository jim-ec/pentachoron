package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import io.jim.tesserapp.util.BYTE_LENGTH

open class GlBuffer(
        val target: Int
) {

    val bufferHandle = resultCode { GLES30.glGenBuffers(1, resultCode) }

    fun allocate(floatCapacity: Int, initialData: java.nio.Buffer?, usage: Int = GLES30.GL_STATIC_DRAW) {
        GLES30.glBufferData(target, floatCapacity * Float.BYTE_LENGTH, initialData, usage)
    }

    inline fun bound(f: () -> Unit) {
        GLES30.glBindBuffer(target, bufferHandle)
        f()
        GLES30.glBindBuffer(target, 0)
    }

    /*
    fun write(data: java.nio.Buffer, size: Int = floatCapacity, offset: Int = 0) {
        GLES30.glBufferSubData(target, offset, size * Float.BYTE_LENGTH, data)
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
    */

}
