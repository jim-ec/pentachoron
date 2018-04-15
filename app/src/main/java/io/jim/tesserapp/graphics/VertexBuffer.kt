package io.jim.tesserapp.graphics

import android.opengl.GLES20.*
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertTrue
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Vertex buffer.
 */
data class VertexBuffer(private val maxVertices: Int) {

    companion object {

        private const val COMPONENTS_PER_POSITION = 3
        private const val COMPONENTS_PER_COLOR = 3
        private const val COMPONENTS_PER_MODEL_INDEX = 1

        private const val COMPONENTS_PER_VERTEX = COMPONENTS_PER_POSITION +
                COMPONENTS_PER_COLOR +
                COMPONENTS_PER_MODEL_INDEX

        private const val FLOAT_BYTE_LENGTH = 4
        private const val VERTEX_BYTE_LENGTH = COMPONENTS_PER_VERTEX * FLOAT_BYTE_LENGTH

    }

    private val handle = let {
        val status = IntArray(1)
        glGenBuffers(1, status, 0)
        status[0]
    }

    private val byteBuffer = ByteBuffer.allocateDirect(maxVertices * VERTEX_BYTE_LENGTH).apply {
        order(ByteOrder.nativeOrder())
    }

    private val floatBuffer = byteBuffer.asFloatBuffer().apply {
        clear()
        while (position() < capacity()) put(0f)
        rewind()
    }

    /**
     * Add a vertex with a given attribute set of a [position], a [color] and its [modelIndex]
     * into the model matrix array.
     */
    fun appendVertex(position: Vector, color: Color, modelIndex: Int) {
        assertTrue(
                "Insufficient localMemory to store vertex:  pos=%d(%d verts) cap=%d(%d verts) needed=%d"
                        .format(floatBuffer.position(),
                                floatBuffer.position() / COMPONENTS_PER_VERTEX,
                                floatBuffer.capacity(),
                                floatBuffer.capacity() / COMPONENTS_PER_VERTEX,
                                COMPONENTS_PER_VERTEX),
                floatBuffer.position() + COMPONENTS_PER_VERTEX <= floatBuffer.capacity())

        floatBuffer.apply {
            put(position.x.toFloat())
            put(position.y.toFloat())
            put(position.z.toFloat())
            put(color.red)
            put(color.green)
            put(color.blue)
            put(modelIndex.toFloat())
        }
    }

    /**
     * Bind the vertex buffer and instruct the vertex attribute pointer a the given [shader].
     */
    fun bind(shader: Shader) {
        floatBuffer.rewind()
        glBindBuffer(GL_ARRAY_BUFFER, handle)
        glBufferData(GL_ARRAY_BUFFER, maxVertices * VERTEX_BYTE_LENGTH, floatBuffer, GL_STATIC_DRAW)

        // Position attribute:
        glEnableVertexAttribArray(shader.positionAttributeLocation)
        glVertexAttribPointer(
                shader.positionAttributeLocation,
                COMPONENTS_PER_POSITION,
                GL_FLOAT,
                false,
                VERTEX_BYTE_LENGTH,
                0
        )

        // Color attribute:
        glEnableVertexAttribArray(shader.colorAttributeLocation)
        glVertexAttribPointer(
                shader.colorAttributeLocation,
                COMPONENTS_PER_COLOR,
                GL_FLOAT,
                false,
                VERTEX_BYTE_LENGTH,
                COMPONENTS_PER_POSITION * FLOAT_BYTE_LENGTH
        )

        // Model index attribute:
        glEnableVertexAttribArray(shader.modelIndexAttributeLocation)
        glVertexAttribPointer(
                shader.modelIndexAttributeLocation,
                COMPONENTS_PER_MODEL_INDEX,
                GL_FLOAT,
                false,
                VERTEX_BYTE_LENGTH,
                (COMPONENTS_PER_POSITION + COMPONENTS_PER_COLOR) * FLOAT_BYTE_LENGTH
        )
    }

}