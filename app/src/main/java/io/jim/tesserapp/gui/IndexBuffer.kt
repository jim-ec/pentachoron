package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import junit.framework.Assert.assertTrue
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class IndexBuffer(val size: Int, var baseIndex: Int = 0) {

    companion object {
        const val INDEX_BYTE_LENGTH = 4
    }

    private val handle = let {
        val status = IntArray(1)
        glGenBuffers(1, status, 0)
        status[0]
    }

    private val byteBuffer = ByteBuffer.allocateDirect(size * INDEX_BYTE_LENGTH).apply {
        order(ByteOrder.nativeOrder())
    }

    private val intBuffer = byteBuffer.asIntBuffer().apply {
        clear()
        while (position() < capacity()) put(0)
        rewind()
    }

    fun appendIndex(index: Int) {
        assertTrue("Insufficient memory to store vertex: pos=%d  cap=%d".format(intBuffer.position(), intBuffer.capacity()),
                intBuffer.position() < intBuffer.capacity())
        intBuffer.put(baseIndex + index)
    }

    fun bind() {
        intBuffer.rewind()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handle)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size * INDEX_BYTE_LENGTH, intBuffer, GL_DYNAMIC_DRAW)
    }

}