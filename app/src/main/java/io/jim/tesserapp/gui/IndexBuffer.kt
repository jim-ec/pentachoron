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

    private data class GeometryEntry(val indexOffset: Int, val indexCount: Int)

    private var recording = false
    private var globalIndex = 0
    private var localIndexCounter = 0
    internal var globalIndexCounter = 0
    private var globalIndicesOffset = 0
    private val geometryRegistry = ArrayList<GeometryEntry>()
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
        intBuffer.put(globalIndicesOffset + index)
        localIndexCounter++
    }

    fun bind() {
        assertFalse("Index buffer is currently recording", recording)
        intBuffer.rewind()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handle)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size * INDEX_BYTE_LENGTH, intBuffer, GL_DYNAMIC_DRAW)
    }

    fun startRecording() {
        assertFalse("Index buffer is already recording", recording)
        geometryRegistry.clear()
        globalIndicesOffset = 0
        globalIndex = 0
        localIndexCounter = 0
        globalIndexCounter = 0
        recording = true
    }

    fun endRecording() {
        assertTrue("Index buffer is not recording", recording)
        assertEquals("Geometry is not committed", 0, localIndexCounter)
        recording = false
    }

    fun commitGeometry(geometry: Geometry) {
        assertTrue("Index buffer is not recording", recording)
        geometryRegistry.add(GeometryEntry(globalIndexCounter, localIndexCounter))
        globalIndexCounter += localIndexCounter
        globalIndicesOffset += geometry.points.size
        localIndexCounter = 0
    }

    fun forEachGeometry(f: (index: Int, indexOffset: Int, indexCount: Int) -> Unit) {
        geometryRegistry.forEachIndexed { index, entry ->
            f(index, entry.indexOffset, entry.indexCount)
        }
    }

}