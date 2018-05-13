package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import io.jim.tesserapp.util.BYTE_LENGTH
import java.nio.ByteBuffer

open class GlBuffer(
        val target: Int
) {

    val bufferHandle = resultCode { GLES30.glGenBuffers(1, resultCode) }

    /**
     * Count of floats currently being allocated.
     */
    var floatSize = 0
        private set

    fun allocate(floatCapacity: Int, initialData: java.nio.Buffer?, usage: Int = GLES30.GL_STATIC_DRAW) {
        /*GLES30.glBufferData(
                target,
                floatCapacity * Float.BYTE_LENGTH,
                initialData,
                usage
        )
        floatSize = floatCapacity*/

        GLES30.glBufferData(
                target,
                floatCapacity * Float.BYTE_LENGTH,
                null,
                usage
        )
        floatSize = floatCapacity

        mapped(GLES30.GL_MAP_WRITE_BIT) { byteBuffer ->

        }
    }

    inline fun bound(f: () -> Unit) {
        GLES30.glBindBuffer(target, bufferHandle)
        f()
        GLES30.glBindBuffer(target, 0)
    }

    inline fun read(componentCounts: Int, f: (components: List<Float>, index: Int) -> Unit) {
        bound {

            mapped(GLES30.GL_MAP_READ_BIT) { byteBuffer ->

                val buffer = byteBuffer.asFloatBuffer()

                for (i in 0 until buffer.capacity()) {
                    println("Float[$i] = ${buffer[i]}")
                }

                if (buffer.capacity() % componentCounts != 0)
                    throw RuntimeException("Read buffer size is not a multiple of $componentCounts")

                val list = MutableList(componentCounts) { 0f }

                for (i in 0 until buffer.capacity() step componentCounts) {
                    for (c in 0 until componentCounts) {
                        list[c] = buffer[i + c]
                    }
                    f(list, i / componentCounts)
                }

            }
        }
    }

    inline fun mapped(access: Int, f: (byteBuffer: java.nio.ByteBuffer) -> Unit) {
        val mappedBuffer = GLES30.glMapBufferRange(
                target,
                0,
                floatSize * Float.BYTE_LENGTH,
                access
        )
                ?: throw GlException("Cannot map buffer")

        f(mappedBuffer as ByteBuffer)

        GLES30.glUnmapBuffer(target)
        GlException.check("Cannot un-map buffer")
    }

    /*
    fun write(data: java.nio.Buffer, size: Int = floatCapacity, offset: Int = 0) {
        GLES30.glBufferSubData(target, offset, size * Float.BYTE_LENGTH, data)
    }
    */

}
