package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import io.jim.tesserapp.geometry.Geometry
import junit.framework.Assert.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class IndexBuffer(
        val size: Int) {

    companion object {
        const val INDEX_BYTE_LENGTH = 4
    }

    private var recording = false
    private var globalIndex = 0
    private var counter = 0
    private var indexOffset = 0
    private val geometryOffsets = ArrayList<Int>()
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
        assertTrue("Index buffer is not recording", recording)
        assertTrue("Insufficient memory to store vertex: pos=%d  cap=%d".format(intBuffer.position(), intBuffer.capacity()),
                intBuffer.position() < intBuffer.capacity())
        intBuffer.put(indexOffset + index)
    }

    fun bind() {
        assertFalse("Index buffer is currently recording", recording)
        intBuffer.rewind()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handle)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size * INDEX_BYTE_LENGTH, intBuffer, GL_DYNAMIC_DRAW)
    }

    fun startRecording() {
        assertFalse("Index buffer is already recording", recording)
        geometryOffsets.clear()
        indexOffset = 0
        globalIndex = 0
        counter = 0
        recording = true
    }

    fun endRecording() {
        assertTrue("Index buffer is not recording", recording)
        assertEquals("Geometry is not committed", 0, counter)
        recording = false
        println("End recording, geometry offsets: $geometryOffsets")
    }

    fun commitGeometry(geometry: Geometry) {
        assertTrue("Index buffer is not recording", recording)
        geometryOffsets.add(indexOffset)
        indexOffset += geometry.points.size
    }

}