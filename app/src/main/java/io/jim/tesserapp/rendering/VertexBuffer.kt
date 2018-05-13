package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.rendering.engine.GlBuffer
import io.jim.tesserapp.rendering.engine.GlVertexBuffer
import io.jim.tesserapp.util.InputStreamBuffer

/**
 * Uploads buffer data to an OpenGL vertex buffer.
 */
class VertexBuffer(
        shader: Shader,
        val backingPositionBuffer: InputStreamBuffer,
        val backingColorBuffer: InputStreamBuffer,
        val backingModelIndexBuffer: InputStreamBuffer
) : GlVertexBuffer() {

    val positionBuffer = GlBuffer(GLES30.GL_ARRAY_BUFFER)
    val colorBuffer = GlBuffer(GLES30.GL_ARRAY_BUFFER)
    val modelIndexBuffer = GlBuffer(GLES30.GL_ARRAY_BUFFER)

    companion object {
        const val FLOATS_PER_POSITION = 3
        const val FLOATS_PER_COLOR = 3
        const val FLOATS_PER_MODEL_INDEX = 1
    }

    init {
        // Instruct VAO:
        vertexArrayBound {

            positionBuffer.bound {

                // Position attribute:
                GLES30.glEnableVertexAttribArray(shader.positionAttributeLocation)
                GLES30.glVertexAttribPointer(
                        shader.positionAttributeLocation,
                        FLOATS_PER_POSITION,
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
                        FLOATS_PER_COLOR,
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
                        FLOATS_PER_MODEL_INDEX,
                        GLES30.GL_FLOAT,
                        false,
                        0,
                        0
                )
            }
        }
    }

    /**
     * Upload data from backing buffers.
     */
    override fun write() {
        positionBuffer.bound {
            positionBuffer.allocate(
                    backingPositionBuffer.writtenElementCounts * FLOATS_PER_POSITION,
                    backingPositionBuffer.floatBuffer
            )

            println("Read back positions:")
            positionBuffer.read(3) { components, index ->
                val position = Vector3d(components[0], components[1], components[2])
                println("Position[$index]:   $position")
            }
        }

        colorBuffer.bound {
            colorBuffer.allocate(
                    backingColorBuffer.writtenElementCounts * FLOATS_PER_COLOR,
                    backingColorBuffer.floatBuffer
            )
        }

        modelIndexBuffer.bound {
            positionBuffer.allocate(
                    backingModelIndexBuffer.writtenElementCounts * FLOATS_PER_MODEL_INDEX,
                    backingModelIndexBuffer.floatBuffer
            )
        }
    }

}
