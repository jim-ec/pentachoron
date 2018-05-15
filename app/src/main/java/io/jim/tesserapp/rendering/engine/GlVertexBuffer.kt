package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Provide a VAO.
 */
open class GlVertexBuffer(
        val backingBuffer: InputStreamBuffer,
        val drawMode: Int
) : GlBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_STATIC_DRAW) {

    /**
     * Store vertex attribute pointers.
     */
    val vertexArray = resultCode { GLES30.glGenVertexArrays(1, resultCode) }

    /**
     * Invokes [f] while the internal VAO is being bound.
     * Do not bind any other VAO during this call.
     * @throws RuntimeException
     */
    fun vertexArrayBound(f: () -> Unit) {

        if (0 != resultCode { GLES30.glGetIntegerv(GLES30.GL_VERTEX_ARRAY_BINDING, resultCode) })
            throw RuntimeException("Cannot bind VAO, another one is currently bound")

        GLES30.glBindVertexArray(vertexArray)
        f()
        GLES30.glBindVertexArray(0)
    }

    /**
     * Upload data from backing buffers.
     */
    fun write() {
        allocate(
                backingBuffer.writtenVectorCounts,
                backingBuffer.floatBuffer
        )
    }

    /**
     * Draw vertex data with [drawMode].
     * Counts is determined through [backingBuffer]'s [InputStreamBuffer.writtenElementCounts].
     */
    fun draw() {
        vertexArrayBound {
            GLES30.glDrawArrays(
                    drawMode,
                    0,
                    backingBuffer.writtenElementCounts)
            GlException.check("Draw vertex buffer")
        }
    }

}
