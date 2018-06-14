package io.jim.tesserapp.graphics

import android.opengl.GLES30
import io.jim.tesserapp.graphics.engine.GlVertexBuffer
import io.jim.tesserapp.util.BYTE_LENGTH

/**
 * A specific vertex buffer drawing lines, defining vertex data layout.
 */
class VertexBuffer(shader: Shader) : GlVertexBuffer(GLES30.GL_LINES) {

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
         */
        const val ATTRIBUTE_COUNTS = 2

        /**
         * Floats taken by one complete vertex.
         */
        private const val VERTEX_FLOATS = ATTRIBUTE_COUNTS * ATTRIBUTE_FLOATS

        /**
         * Bytes taken by one complete vertex.
         */
        private val BYTES_PER_VERTEX =
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
            }
        }
    }

}
