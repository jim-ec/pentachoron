package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.rendering.engine.GlBuffer
import io.jim.tesserapp.rendering.engine.resultCode
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer(shader: Shader, val backingBuffer: InputStreamBuffer)
    : GlBuffer(GLES30.GL_ARRAY_BUFFER) {

    /**
     * Store vertex attribute pointers.
     */
    val vertexArray = resultCode { GLES30.glGenVertexArrays(1, resultCode) }

    init {
        bound {
            // Instruct VAO:
            vertexArrayBound {

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

    fun vertexArrayBound(f: () -> Unit) {
        GLES30.glBindVertexArray(vertexArray)
        f()
        GLES30.glBindVertexArray(0)
    }

    /**
     * Upload data from [backingBuffer].
     */
    fun write() {
        bound {
            allocate(
                    backingBuffer.writtenElementCounts * Vertex.COMPONENTS_PER_VERTEX,
                    backingBuffer.floatBuffer
            )
        }
    }

}
