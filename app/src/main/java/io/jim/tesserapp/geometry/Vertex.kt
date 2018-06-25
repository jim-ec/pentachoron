package io.jim.tesserapp.geometry

import android.opengl.GLES20
import io.jim.tesserapp.graphics.Shader
import io.jim.tesserapp.graphics.engine.GlVertexBuffer
import io.jim.tesserapp.math.vector.VectorN
import io.jim.tesserapp.util.BYTE_LENGTH

data class Vertex(
        val position: VectorN,
        val color: Int
)

fun generateVertexBuffer(shader: Shader) = GlVertexBuffer(GLES20.GL_LINES) {
    // Position attribute:
    GLES20.glEnableVertexAttribArray(shader.positionAttributeLocation)
    GLES20.glVertexAttribPointer(
            shader.positionAttributeLocation,
            ATTRIBUTE_FLOATS,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            OFFSET_POSITION
    )
    
    // Color attribute:
    GLES20.glEnableVertexAttribArray(shader.colorAttributeLocation)
    GLES20.glVertexAttribPointer(
            shader.colorAttributeLocation,
            ATTRIBUTE_FLOATS,
            GLES20.GL_FLOAT,
            false,
            STRIDE,
            OFFSET_COLOR
    )
}

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
const val VERTEX_FLOATS = ATTRIBUTE_COUNTS * ATTRIBUTE_FLOATS

/**
 * Bytes taken by one complete vertex.
 */
val BYTES_PER_VERTEX =
        VERTEX_FLOATS * Float.BYTE_LENGTH

/**
 * Vertex stride, in bytes.
 */
val STRIDE =
        BYTES_PER_VERTEX

/**
 * Position attribute offset, in bytes.
 */
val OFFSET_POSITION =
        0 * Float.BYTE_LENGTH

/**
 * Color attribute offset, in bytes.
 */
val OFFSET_COLOR =
        OFFSET_POSITION + ATTRIBUTE_FLOATS * Float.BYTE_LENGTH
