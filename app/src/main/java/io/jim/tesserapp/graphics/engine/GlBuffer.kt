package io.jim.tesserapp.graphics.engine

import android.opengl.GLES30
import io.jim.tesserapp.math.vector.Vector4dh
import io.jim.tesserapp.util.BYTE_LENGTH
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * GL Buffer.
 *
 * Does not actually store memory, since e.g. reading causes a buffer to be created through
 * mapping.
 *
 * @property target The GL target this buffer is bound to, e.g. [GLES30.GL_ARRAY_BUFFER].
 * @property usage How this buffer is intended to be used, see [GLES30.glBufferData].
 */
open class GlBuffer(
        val target: Int,
        private val usage: Int
) {

    /**
     * The actual handle retrieved from GL.
     */
    val bufferHandle = resultCode { GLES30.glGenBuffers(1, resultCode) }

    /**
     * Buffer size in bytes.
     */
    inline val byteCount: Int
        get() = resultCode {
            bound {
                GLES30.glGetBufferParameteriv(target, GLES30.GL_BUFFER_SIZE, resultCode)
            }
        }

    /**
     * Buffer size in floats.
     */
    inline val floatCount: Int
        get() = byteCount / 4

    /**
     * Maps GL targets to their binding-query constant counterparts.
     */
    inline val binding: Int
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
            GlException.check("Allocate memory memory")
        }
    }

    /**
     * Calls [f] while this buffer is bound to its [target].
     */
    inline fun bound(crossinline f: () -> Unit) {
        val oldBinding = resultCode { GLES30.glGetIntegerv(binding, resultCode) }
        GLES30.glBindBuffer(target, bufferHandle)
        f()
        GLES30.glBindBuffer(target, oldBinding)
    }

    /**
     * Read the buffer contents out by calling [f] successively
     * for each collection of vectors.
     *
     * @param vectorsPerInvocation Defines of how  many vectors a single
     *                             invocation of [f] expect to take.
     *
     * @param f Takes the current list of vectors, as well as the index
     *          of the current invocation.
     *
     * @throws RuntimeException If the mapped buffer's size is not a multiple of four floats.
     */
    inline fun read(vectorsPerInvocation: Int, crossinline f: (vectors: List<Vector4dh>, index: Int) -> Unit) {
        mapped(GLES30.GL_MAP_READ_BIT) { byteBuffer ->

            val buffer = byteBuffer.asFloatBuffer()

            if (buffer.capacity() % (vectorsPerInvocation * 4) != 0)
                throw RuntimeException("Read memory size=${buffer.capacity()} is not a multiple of ${vectorsPerInvocation * 4}")

            val list = MutableList(vectorsPerInvocation) { Vector4dh() }

            for (i in 0 until buffer.capacity() step (vectorsPerInvocation * 4)) {
                for (c in 0 until vectorsPerInvocation) {
                    list[c].x = buffer[i + c * 4 + 0].toDouble()
                    list[c].y = buffer[i + c * 4 + 1].toDouble()
                    list[c].z = buffer[i + c * 4 + 2].toDouble()
                    list[c].q = buffer[i + c * 4 + 3].toDouble()
                }
                f(list, i / (vectorsPerInvocation * 4))
            }

        }
    }

    /**
     * Calls [f] while this buffer is being mapped.
     * After [f] returns, the buffer is automatically unmapped again.
     *
     * @param access Access flags for the mapping, like in [GLES30.glMapBufferRange].
     * @param f Gets the mapped buffer.
     *
     * @throws GlException If buffer cannot be mapped, thrown before calling [f].
     * @throws GlException If buffer cannot be unmapped, thrown after calling [f].
     */
    inline fun mapped(access: Int, crossinline f: (byteBuffer: java.nio.ByteBuffer) -> Unit) {
        bound {

            val mappedBuffer = GLES30.glMapBufferRange(target, 0, byteCount, access)
                    ?: throw GlException("Cannot map memory")

            mappedBuffer as ByteBuffer
            mappedBuffer.order(ByteOrder.nativeOrder())

            f(mappedBuffer)

            GLES30.glUnmapBuffer(target)
            GlException.check("Cannot un-map memory")
        }
    }

}
