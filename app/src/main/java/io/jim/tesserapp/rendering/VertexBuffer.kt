package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.rendering.engine.GlVertexBuffer
import io.jim.tesserapp.util.BYTE_LENGTH
import io.jim.tesserapp.util.InputStreamMemory

/**
 * A specific vertex buffer drawing lines, defining vertex data layout.
 */
class VertexBuffer(
        shader: Shader,
        memory: InputStreamMemory
) : GlVertexBuffer(memory, GLES30.GL_LINES) {

    companion object {

        /**
         * Floats take by one attribute.
         * Due to alignment, each attribute has 4 float values, regardless of how many
         * it actually uses.
         */
        const val ATTRIBUTE_FLOATS = 4

        /**
         * Counts of different attributes.
         * - Position
         * - Color
         * - Model index
         */
        const val ATTRIBUTE_COUNTS = 3

        /**
         * Padding floats in front of the model index, to keep 4-float alignment.
         */
        const val MODEL_INDEX_PADDING = 3

        /**
         * Floats taken by one complete vertex.
         */
        const val VERTEX_FLOATS = ATTRIBUTE_COUNTS * ATTRIBUTE_FLOATS

        /**
         * Bytes taken by one complete vertex.
         */
        val BYTES_PER_VERTEX =
                VERTEX_FLOATS * Float.BYTE_LENGTH

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
                OFFSET_POSITION + ATTRIBUTE_FLOATS * Float.BYTE_LENGTH

        /**
         * Model index attribute offset, in bytes.
         */
        private val OFFSET_MODEL_INDEX =
                OFFSET_COLOR + (ATTRIBUTE_FLOATS + MODEL_INDEX_PADDING) * Float.BYTE_LENGTH
    }

    init {
        // Instruct VAO:
        vertexArrayBound {

            bound {

                // Position attribute:
                GLES30.glEnableVertexAttribArray(shader.positionAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.positionAttributeLocation,
                        ATTRIBUTE_FLOATS,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_POSITION
                )

                // Color attribute:
                GLES30.glEnableVertexAttribArray(shader.colorAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.colorAttributeLocation,
                        ATTRIBUTE_FLOATS,
                        GLES30.GL_FLOAT,
                        false,
                        STRIDE,
                        OFFSET_COLOR
                )

                // Model index attribute:
                GLES30.glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
                GLES30.glVertexAttribIPointer(
                        shader.modelIndexAttributeLocation,
                        ATTRIBUTE_FLOATS,
                        GLES30.GL_INT,
                        STRIDE,
                        OFFSET_MODEL_INDEX
                )
            }
        }
    }

}
