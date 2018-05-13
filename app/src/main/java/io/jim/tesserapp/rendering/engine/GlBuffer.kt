package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import io.jim.tesserapp.util.BYTE_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder

open class GlBuffer(
        val target: Int
) {

    val bufferHandle = resultCode { GLES30.glGenBuffers(1, resultCode) }

    /**
     * Buffer size in bytes.
     */
    val size: Int
        get() = resultCode {
            GLES30.glGetBufferParameteriv(target, GLES30.GL_BUFFER_SIZE, resultCode)
        }

    /**
     * Allocate memory and optionally write data in.
     * @param floatCapacity Counts of float to be written.
     * @param data Data to be written.
     * @param usage Usage flags of memory.
     */
    fun allocate(floatCapacity: Int, data: java.nio.Buffer?, usage: Int) {
        GLES30.glBufferData(
                target,
                floatCapacity * Float.BYTE_LENGTH,
                data,
                usage
        )
    }

    /**
     * Calls [f] while this buffer is bound to its [target].
     * Rebinding [target] within [f] is discouraged, as that can lead to hard-to-find bugs.
     */
    inline fun bound(f: () -> Unit) {
        GLES30.glBindBuffer(target, bufferHandle)
        f()
        GLES30.glBindBuffer(target, 0)
    }

    /**
     * Read the buffer contents out by calling [f] successively for each data entry.
     * @param floatsPerEntry Defines of how  many floats a single invocation of [f] expect to take.
     * @param f Takes a the current list of floats, as well as the index of the current entry.
     */
    inline fun read(floatsPerEntry: Int, f: (floats: List<Float>, index: Int) -> Unit) {
        bound {

            mapped(GLES30.GL_MAP_READ_BIT) { byteBuffer ->

                val buffer = byteBuffer.asFloatBuffer()

                if (buffer.capacity() % floatsPerEntry != 0)
                    throw RuntimeException("Read buffer size is not a multiple of $floatsPerEntry")

                val list = MutableList(floatsPerEntry) { 0f }

                for (i in 0 until buffer.capacity() step floatsPerEntry) {
                    for (c in 0 until floatsPerEntry) {
                        list[c] = buffer[i + c]
                    }
                    f(list, i / floatsPerEntry)
                }

            }
        }
    }

    /**
     * Calls [f] while this buffer is being mapped.
     * @param access Access flags for the mapping, like in [GLES30.glMapBufferRange].
     * @param f Takes the mapped buffer.
     */
    inline fun mapped(access: Int, f: (byteBuffer: java.nio.ByteBuffer) -> Unit) {

        val mappedBuffer = GLES30.glMapBufferRange(target, 0, size, access)
                ?: throw GlException("Cannot map buffer")

        mappedBuffer as ByteBuffer
        mappedBuffer.order(ByteOrder.nativeOrder())

        f(mappedBuffer)

        GLES30.glUnmapBuffer(target)
        GlException.check("Cannot un-map buffer")
    }

}
