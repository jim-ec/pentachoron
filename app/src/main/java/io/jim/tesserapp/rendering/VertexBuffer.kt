package io.jim.tesserapp.rendering

import android.opengl.GLES20.*
import io.jim.tesserapp.graphics.FillUpBuffer
import io.jim.tesserapp.graphics.GeometryManager

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
    fun bind(shader: Shader, backingBuffer: FillUpBuffer) {
        println("Upload ${backingBuffer.activeEntries} vertices")
        backingBuffer.rewind()
        glBindBuffer(GL_ARRAY_BUFFER, handle)
        glBufferData(GL_ARRAY_BUFFER, backingBuffer.byteCapacity, backingBuffer.floatBuffer, GL_STATIC_DRAW)

        val (colorOffset, modelIndexOffset) = backingBuffer.layout.byteRanges

        // Position attribute:
        glEnableVertexAttribArray(shader.positionAttributeLocation)
        glVertexAttribPointer(
                shader.positionAttributeLocation,
                GeometryManager.COMPONENTS_PER_POSITION,
                GL_FLOAT,
                false,
                backingBuffer.layout.byteLength,
                0
        )

        // Color attribute:
        glEnableVertexAttribArray(shader.colorAttributeLocation)
        glVertexAttribPointer(
                shader.colorAttributeLocation,
                GeometryManager.COMPONENTS_PER_COLOR,
                GL_FLOAT,
                false,
                backingBuffer.layout.byteLength,
                //GeometryManager.COMPONENTS_PER_POSITION * FillUpBuffer.FLOAT_BYTE_LENGTH
                colorOffset
        )

        // Model index attribute:
        glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
        glVertexAttribPointer(
                shader.modelIndexAttributeLocation,
                GeometryManager.COMPONENTS_PER_MODEL_INDEX,
                GL_FLOAT,
                false,
                backingBuffer.layout.byteLength,
                //(GeometryManager.COMPONENTS_PER_POSITION + GeometryManager.COMPONENTS_PER_COLOR) * FillUpBuffer.FLOAT_BYTE_LENGTH
                colorOffset + modelIndexOffset
        )
    }

}