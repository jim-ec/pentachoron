package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.rendering.engine.GlBuffer
import io.jim.tesserapp.rendering.engine.GlVertexBuffer
import io.jim.tesserapp.util.BYTE_LENGTH
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer(
        shader: Shader,
        val backingBuffer: InputStreamBuffer
) : GlVertexBuffer() {

    val vertexBuffer = GlBuffer(GLES30.GL_ARRAY_BUFFER)

    companion object {

        /**
         * Floats taken by one position attribute.
         */
        const val FLOATS_PER_POSITION = 3

        /**
         * Floats taken by one color attribute.
         */
        const val FLOATS_PER_COLOR = 3

        /**
         * Floats taken by one model index attribute.
         */
        const val FLOATS_PER_MODEL_INDEX = 1

        /**
         * Floats taken by one complete vertex.
         */
        const val FLOATS_PER_VERTEX = FLOATS_PER_POSITION + FLOATS_PER_COLOR + FLOATS_PER_MODEL_INDEX

        /**
         * Vertex stride, in bytes.
         */
        val STRIDE = FLOATS_PER_VERTEX * Float.BYTE_LENGTH

        /**
         * Position attribute offset, in bytes.
         */
        val OFFSET_POSITION = 0 * Float.BYTE_LENGTH

        /**
         * Color attribute offset, in bytes.
         */
        val OFFSET_COLOR = OFFSET_POSITION + FLOATS_PER_POSITION * Float.BYTE_LENGTH

        /**
         * Model index attribute offset, in bytes.
         */
        val OFFSET_MODEL_INDEX = OFFSET_COLOR + FLOATS_PER_COLOR * Float.BYTE_LENGTH
    }

    init {
        // Instruct VAO:
        vertexArrayBound {

            vertexBuffer.bound {

                // Position attribute:
                GLES30.glEnableVertexAttribArray(shader.positionAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.positionAttributeLocation,
                        FLOATS_PER_POSITION,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_POSITION
                )

                // Color attribute:
                GLES30.glEnableVertexAttribArray(shader.colorAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.colorAttributeLocation,
                        FLOATS_PER_COLOR,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_COLOR
                )

                // Model index attribute:
                GLES30.glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.modelIndexAttributeLocation,
                        FLOATS_PER_MODEL_INDEX,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_MODEL_INDEX
                )
            }
        }
    }

    /**
     * Upload data from backing buffers.
     */
    override fun write() {
        vertexBuffer.bound {
            vertexBuffer.allocate(
                    backingBuffer.writtenElementCounts * FLOATS_PER_VERTEX,
                    backingBuffer.floatBuffer,
                    GLES30.GL_STATIC_DRAW
            )
        }
    }

}
