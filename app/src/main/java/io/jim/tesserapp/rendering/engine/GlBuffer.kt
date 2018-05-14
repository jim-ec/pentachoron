package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import io.jim.tesserapp.math.vector.Vector4d
import io.jim.tesserapp.util.BYTE_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder

open class GlBuffer(
        val target: Int,
        val usage: Int
) {

    val bufferHandle = resultCode { GLES30.glGenBuffers(1, resultCode) }

    /**
     * Buffer size in bytes.
     */
    val size: Int
        get() = resultCode {
            bound {
                GLES30.glGetBufferParameteriv(target, GLES30.GL_BUFFER_SIZE, resultCode)
            }
        }

    /**
     * Buffer size in floats.
     */
    val floatCount: Int
        get() = size / 4

    val binding: Int
        get() = when (target) {
            GLES30.GL_ARRAY_BUFFER -> GLES30.GL_ARRAY_BUFFER_BINDING
            GLES30.GL_ELEMENT_ARRAY_BUFFER -> GLES30.GL_ELEMENT_ARRAY_BUFFER_BINDING
            else -> throw RuntimeException("Unknown binding known for target $target")
        }

    /**
     * Allocate memory and optionally write data in.
     * @param vectorCapacity Counts of 4d vectors to be written.
     * @param data Data to be written.
     */
    open fun allocate(vectorCapacity: Int, data: java.nio.Buffer? = null) {
        bound {
            GLES30.glBufferData(
                    target,
                    vectorCapacity * 4 * Float.BYTE_LENGTH,
                    data,
                    usage
            )
            GlException.check("Allocate buffer memory")
        }
    }

    /**
     * Calls [f] while this buffer is bound to its [target].
     */
    inline fun bound(f: () -> Unit) {
        val oldBinding = resultCode { GLES30.glGetIntegerv(binding, resultCode) }
        GLES30.glBindBuffer(target, bufferHandle)
        f()
        GLES30.glBindBuffer(target, oldBinding)
    }

    /**
     * Read the buffer contents out by calling [f] successively for each collection of vectors.
     * @param vectorsPerInvocation Defines of how  many vectors a single invocation of [f] expect to take.
     * @param f Takes the current list of vectors, as well as the index of the current invocation.
     */
    inline fun read(vectorsPerInvocation: Int, f: (vectors: List<Vector4d>, index: Int) -> Unit) {
        mapped(GLES30.GL_MAP_READ_BIT) { byteBuffer ->

            val buffer = byteBuffer.asFloatBuffer()

            if (buffer.capacity() % (vectorsPerInvocation * 4) != 0)
                throw RuntimeException("Read buffer size=${buffer.capacity()} is not a multiple of ${vectorsPerInvocation * 4}")

            val list = MutableList(vectorsPerInvocation) { Vector4d() }

            for (i in 0 until buffer.capacity() step (vectorsPerInvocation * 4)) {
                for (c in 0 until vectorsPerInvocation) {
                    list[c].x = buffer[i + c * 4 + 0]
                    list[c].y = buffer[i + c * 4 + 1]
                    list[c].z = buffer[i + c * 4 + 2]
                    list[c].q = buffer[i + c * 4 + 3]
                }
                f(list, i / (vectorsPerInvocation * 4))
            }

        }
    }

    /**
     * Calls [f] while this buffer is being mapped.
     * @param access Access flags for the mapping, like in [GLES30.glMapBufferRange].
     * @param f Takes the mapped buffer.
     */
    inline fun mapped(access: Int, f: (byteBuffer: java.nio.ByteBuffer) -> Unit) {
        bound {

            val mappedBuffer = GLES30.glMapBufferRange(target, 0, size, access)
                    ?: throw GlException("Cannot map buffer")

            mappedBuffer as ByteBuffer
            mappedBuffer.order(ByteOrder.nativeOrder())

            f(mappedBuffer)

            GLES30.glUnmapBuffer(target)
            GlException.check("Cannot un-map buffer")
        }
    }

}
