package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Provide a VAO.
 */
open class GlVertexBuffer(
        val backingBuffer: InputStreamBuffer,
        val floatsPerVertex: Int,
        val drawMode: Int
) : GlBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_STATIC_DRAW) {

    /**
     * Store vertex attribute pointers.
     */
    val vertexArray = resultCode { GLES30.glGenVertexArrays(1, resultCode) }

    /**
     * Invokes [f] while the internal VAO is being bound.
     * Do not bind any other VAO during this call.
     */
    fun vertexArrayBound(f: () -> Unit) {
        GLES30.glBindVertexArray(vertexArray)
        f()
        GLES30.glBindVertexArray(0)
    }

    /**
     * Upload data from backing buffers.
     */
    fun write() {
        allocate(
                backingBuffer.writtenElementCounts * floatsPerVertex,
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
