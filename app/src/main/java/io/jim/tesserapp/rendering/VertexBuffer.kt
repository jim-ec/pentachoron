package io.jim.tesserapp.rendering

import android.opengl.GLES20.*
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.util.BYTE_LENGTH
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer {

    private val handle = let {
        val status = IntArray(1)
        glGenBuffers(1, status, 0)
        status[0]
    }

    /**
     * Bind the vertex buffer and instruct the vertex attribute pointer a the given [shader].
     */
    fun bind(shader: Shader, backingBuffer: InputStreamBuffer) {

        glBindBuffer(GL_ARRAY_BUFFER, handle)
        glBufferData(
                GL_ARRAY_BUFFER,
                (backingBuffer.writtenElementCounts) * Vertex.COMPONENTS_PER_VERTEX * Float.BYTE_LENGTH,
                backingBuffer.floatBuffer,
                GL_STATIC_DRAW)

        // Position attribute:
        glEnableVertexAttribArray(shader.positionAttributeLocation)
        glVertexAttribPointer(
                shader.positionAttributeLocation,
                Vertex.COMPONENTS_PER_POSITION,
                GL_FLOAT,
                false,
                Vertex.STRIDE_BYTES,
                Vertex.OFFSET_POSITION_BYTES
        )

        // Color attribute:
        glEnableVertexAttribArray(shader.colorAttributeLocation)
        glVertexAttribPointer(
                shader.colorAttributeLocation,
                Vertex.COMPONENTS_PER_COLOR,
                GL_FLOAT,
                false,
                Vertex.STRIDE_BYTES,
                Vertex.OFFSET_COLOR_BYTES
        )

        // Model index attribute:
        glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
        glVertexAttribPointer(
                shader.modelIndexAttributeLocation,
                Vertex.COMPONENTS_PER_MODEL_INDEX,
                GL_FLOAT,
                false,
                Vertex.STRIDE_BYTES,
                Vertex.OFFSET_MODEL_INDEX_BYTES
        )
    }

}
