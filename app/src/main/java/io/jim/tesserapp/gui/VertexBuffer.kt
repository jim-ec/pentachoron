package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class VertexBuffer(val size: Int) {

    companion object {
        private const val COMPONENTS_PER_POSITION = 3
        private const val COMPONENTS_PER_COLOR = 3
        const val COMPONENTS_PER_VERTEX = COMPONENTS_PER_POSITION + COMPONENTS_PER_COLOR
        const val FLOAT_BYTE_LENGTH = 4
        const val VERTEX_BYTE_LENGTH = COMPONENTS_PER_VERTEX * FLOAT_BYTE_LENGTH
    }

    private val handle = let {
        val status = IntArray(1)
        glGenBuffers(1, status, 0)
        status[0]
    }

    private val byteBuffer = ByteBuffer.allocateDirect(size * VERTEX_BYTE_LENGTH).apply {
        order(ByteOrder.nativeOrder())
    }

    private val floatBuffer = byteBuffer.asFloatBuffer().apply {
        clear()
        while (position() < capacity()) put(0f)
        rewind()
    }

    fun appendVertex(position: Vector, color: Color) {
        assertEquals("Position vectors must be 3D", 3, position.dimension)
        assertTrue("Insufficient memory to store vertex: pos=%d(%d verts)  cap=%d(%d verts)  needed=%d"
                .format(floatBuffer.position(), floatBuffer.position() / COMPONENTS_PER_VERTEX,
                        floatBuffer.capacity(), floatBuffer.capacity() / COMPONENTS_PER_VERTEX,
                        COMPONENTS_PER_VERTEX),
                floatBuffer.position() + COMPONENTS_PER_VERTEX <= floatBuffer.capacity())

        floatBuffer.apply {
            put(position.x.toFloat())
            put(position.y.toFloat())
            put(position.z.toFloat())
            put(color.red)
            put(color.green)
            put(color.blue)
        }
    }

    fun bind(shader: Shader) {
        floatBuffer.rewind()
        glBindBuffer(GL_ARRAY_BUFFER, handle)
        glBufferData(GL_ARRAY_BUFFER, size * VERTEX_BYTE_LENGTH, floatBuffer, GL_DYNAMIC_DRAW)

        glEnableVertexAttribArray(shader.positionAttributeLocation)
        glVertexAttribPointer(shader.positionAttributeLocation, COMPONENTS_PER_POSITION, GL_FLOAT, false, VERTEX_BYTE_LENGTH, 0)

        glEnableVertexAttribArray(shader.colorAttributeLocation)
        glVertexAttribPointer(shader.colorAttributeLocation, COMPONENTS_PER_COLOR, GL_FLOAT, false, VERTEX_BYTE_LENGTH, COMPONENTS_PER_POSITION * FLOAT_BYTE_LENGTH)
    }

}