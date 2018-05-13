package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.rendering.engine.GlVertexBuffer
import io.jim.tesserapp.util.BYTE_LENGTH
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer(
        shader: Shader,
        backingBuffer: InputStreamBuffer
) : GlVertexBuffer(backingBuffer, FLOATS_PER_VERTEX, GLES30.GL_LINES) {

    companion object {

        /**
         * Floats taken by one position attribute.
         */
        private const val POSITION_FLOATS = 4

        /**
         * Floats taken by one color attribute.
         */
        private const val COLOR_FLOATS = 4

        /**
         * Padding floats in front of the model index, to keep 4-float alignment.
         */
        private const val MODEL_INDEX_PADDING = 3

        /**
         * Floats taken by one model index attribute.
         */
        private const val MODEL_INDEX_FLOATS = 1

        /**
         * Floats taken by one complete vertex.
         */
        const val FLOATS_PER_VERTEX = POSITION_FLOATS +
                COLOR_FLOATS +
                MODEL_INDEX_PADDING +
                MODEL_INDEX_FLOATS

        /**
         * Bytes taken by one complete vertex.
         */
        val BYTES_PER_VERTEX =
                FLOATS_PER_VERTEX * Float.BYTE_LENGTH

        /**
         * Vertex stride, in bytes.
         */
        private val STRIDE =
                BYTES_PER_VERTEX

        /**
         * Position attribute offset, in bytes.
         */
        private val OFFSET_POSITION =
                0 * Float.BYTE_LENGTH

        /**
         * Color attribute offset, in bytes.
         */
        private val OFFSET_COLOR =
                OFFSET_POSITION + POSITION_FLOATS * Float.BYTE_LENGTH

        /**
         * Model index attribute offset, in bytes.
         */
        private val OFFSET_MODEL_INDEX =
                OFFSET_COLOR + (COLOR_FLOATS + MODEL_INDEX_PADDING) * Float.BYTE_LENGTH
    }

    init {
        // Instruct VAO:
        vertexArrayBound {

            bound {

                // Position attribute:
                GLES30.glEnableVertexAttribArray(shader.positionAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.positionAttributeLocation,
                        POSITION_FLOATS,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_POSITION
                )

                // Color attribute:
                GLES30.glEnableVertexAttribArray(shader.colorAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.colorAttributeLocation,
                        COLOR_FLOATS,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_COLOR
                )

                // Model index attribute:
                GLES30.glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.modelIndexAttributeLocation,
                        MODEL_INDEX_FLOATS,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_MODEL_INDEX
                )
            }
        }
    }

}
