package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.rendering.engine.GlBuffer
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer : GlBuffer(GLES30.GL_ARRAY_BUFFER) {

    /**
     * Bind the vertex buffer and instruct the vertex attribute pointer a the given [shader].
     */
    fun instructVertexAttributes(shader: Shader, backingBuffer: InputStreamBuffer) {
        bound {

            allocate(
                    backingBuffer.writtenElementCounts * Vertex.COMPONENTS_PER_VERTEX,
                    backingBuffer.floatBuffer
            )

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
            GLES30.glVertexAttribPointer(
                    shader.modelIndexAttributeLocation,
                    Vertex.COMPONENTS_PER_MODEL_INDEX,
                    GLES30.GL_FLOAT,
                    false,
                    Vertex.STRIDE_BYTES,
                    Vertex.OFFSET_MODEL_INDEX_BYTES
            )
        }
    }

}
