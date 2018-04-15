package io.jim.tesserapp.rendering

import android.opengl.GLES20.*
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.FillUpBuffer
import io.jim.tesserapp.math.Vector

/**
 * Vertex buffer.
 */
data class VertexBuffer(private val maxVertices: Int) {

    companion object {
        private const val COMPONENTS_PER_POSITION = 3
        private const val COMPONENTS_PER_COLOR = 3
        private const val COMPONENTS_PER_MODEL_INDEX = 1
    }

    private val handle = let {
        val status = IntArray(1)
        glGenBuffers(1, status, 0)
        status[0]
    }

    private val memory = FillUpBuffer(maxVertices, FillUpBuffer.Layout(COMPONENTS_PER_POSITION, COMPONENTS_PER_COLOR, COMPONENTS_PER_MODEL_INDEX))

    /**
     * Add a vertex with a given attribute set of a [position], a [color] and its [modelIndex]
     * into the model matrix array.
     */
    fun appendVertex(position: Vector, color: Color, modelIndex: Int) {
        memory += listOf(
                position.x.toFloat(),
                position.y.toFloat(),
                position.z.toFloat(),
                color.red,
                color.green,
                color.blue,
                modelIndex.toFloat()
        )
    }

    /**
     * Bind the vertex buffer and instruct the vertex attribute pointer a the given [shader].
     */
    fun bind(shader: Shader) {
        memory.rewind()
        glBindBuffer(GL_ARRAY_BUFFER, handle)
        glBufferData(GL_ARRAY_BUFFER, memory.byteLength, memory.floatBuffer, GL_STATIC_DRAW)

        // Position attribute:
        glEnableVertexAttribArray(shader.positionAttributeLocation)
        glVertexAttribPointer(
                shader.positionAttributeLocation,
                COMPONENTS_PER_POSITION,
                GL_FLOAT,
                false,
                memory.layout.byteLength,
                0
        )

        // Color attribute:
        glEnableVertexAttribArray(shader.colorAttributeLocation)
        glVertexAttribPointer(
                shader.colorAttributeLocation,
                COMPONENTS_PER_COLOR,
                GL_FLOAT,
                false,
                memory.layout.byteLength,
                COMPONENTS_PER_POSITION * FillUpBuffer.FLOAT_BYTE_LENGTH
        )

        // Model index attribute:
        glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
        glVertexAttribPointer(
                shader.modelIndexAttributeLocation,
                COMPONENTS_PER_MODEL_INDEX,
                GL_FLOAT,
                false,
                memory.layout.byteLength,
                (COMPONENTS_PER_POSITION + COMPONENTS_PER_COLOR) * FillUpBuffer.FLOAT_BYTE_LENGTH
        )
    }

}