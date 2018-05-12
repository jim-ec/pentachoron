package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.util.BYTE_LENGTH
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer {

    private val handle = resultCode { GLES30.glGenBuffers(1, resultCode) }

    /**
     * Bind the vertex buffer and instruct the vertex attribute pointer a the given [shader].
     */
    fun bind(shader: Shader, backingBuffer: InputStreamBuffer) {

        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, handle)
        GLES30.glBufferData(
                GLES30.GL_ARRAY_BUFFER,
                backingBuffer.writtenElementCounts * Vertex.COMPONENTS_PER_VERTEX * Float.BYTE_LENGTH,
                backingBuffer.floatBuffer,
                GLES30.GL_STATIC_DRAW)

        // Position attribute:
        GLES30.glEnableVertexAttribArray(shader.positionAttributeLocation)
        GLES30.glVertexAttribPointer(
                shader.positionAttributeLocation,
                Vertex.COMPONENTS_PER_POSITION,
                GLES30.GL_FLOAT,
                false,
                Vertex.STRIDE_BYTES,
                Vertex.OFFSET_POSITION_BYTES
        )

        // Color attribute:
        GLES30.glEnableVertexAttribArray(shader.colorAttributeLocation)
        GLES30.glVertexAttribPointer(
                shader.colorAttributeLocation,
                Vertex.COMPONENTS_PER_COLOR,
                GLES30.GL_FLOAT,
                false,
                Vertex.STRIDE_BYTES,
                Vertex.OFFSET_COLOR_BYTES
        )

        // Model index attribute:
        GLES30.glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
        GLES30.glVertexAttribIPointer(
                shader.modelIndexAttributeLocation,
                Vertex.COMPONENTS_PER_MODEL_INDEX,
                GLES30.GL_INT,
                Vertex.STRIDE_BYTES,
                Vertex.OFFSET_MODEL_INDEX_BYTES
        )
    }

}
