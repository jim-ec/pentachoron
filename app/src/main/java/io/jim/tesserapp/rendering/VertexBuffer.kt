package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.rendering.engine.GlBuffer
import io.jim.tesserapp.rendering.engine.resultCode
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer(
        shader: Shader,
        val backingPositionBuffer: InputStreamBuffer,
        val backingColorBuffer: InputStreamBuffer,
        val backingModelIndexBuffer: InputStreamBuffer
) {

    /**
     * Store vertex attribute pointers.
     */
    val vertexArray = resultCode { GLES30.glGenVertexArrays(1, resultCode) }

    val positionBuffer = GlBuffer(GLES30.GL_ARRAY_BUFFER)
    val colorBuffer = GlBuffer(GLES30.GL_ARRAY_BUFFER)
    val modelIndexBuffer = GlBuffer(GLES30.GL_ARRAY_BUFFER)

    init {
        // Instruct VAO:
        vertexArrayBound {

            positionBuffer.bound {

                // Position attribute:
                GLES30.glEnableVertexAttribArray(shader.positionAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.positionAttributeLocation,
                        Vertex.COMPONENTS_PER_POSITION,
                        GLES30.GL_FLOAT,
                        false,
                        0,
                        0
                )

            }

            colorBuffer.bound {

                // Color attribute:
                GLES30.glEnableVertexAttribArray(shader.colorAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.colorAttributeLocation,
                        Vertex.COMPONENTS_PER_COLOR,
                        GLES30.GL_FLOAT,
                        false,
                        0,
                        0
                )

            }

            modelIndexBuffer.bound {

                // Model index attribute:
                GLES30.glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.modelIndexAttributeLocation,
                        Vertex.COMPONENTS_PER_MODEL_INDEX,
                        GLES30.GL_FLOAT,
                        false,
                        0,
                        0
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
     * Upload data from backing buffers.
     */
    fun write() {
        positionBuffer.bound {
            positionBuffer.allocate(
                    backingPositionBuffer.writtenElementCounts * Vertex.COMPONENTS_PER_POSITION,
                    backingPositionBuffer.floatBuffer
            )
        }

        colorBuffer.bound {
            colorBuffer.allocate(
                    backingColorBuffer.writtenElementCounts * Vertex.COMPONENTS_PER_COLOR,
                    backingColorBuffer.floatBuffer
            )
        }

        modelIndexBuffer.bound {
            positionBuffer.allocate(
                    backingModelIndexBuffer.writtenElementCounts * Vertex.COMPONENTS_PER_MODEL_INDEX,
                    backingModelIndexBuffer.floatBuffer
            )
        }
    }

}
