package io.jim.tesserapp.graphics

import android.opengl.GLES20.*
import io.jim.tesserapp.geometry.Geometry
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Index buffer.
 */
data class IndexBuffer(private val maxIndices: Int) {

    companion object {
        private const val INDEX_BYTE_LENGTH = 4
    }

    /**
     * The count of currently recorded indices.
     */
    var globalIndexCounter = 0

    private var recording = false
    private var globalIndex = 0
    private var globalIndicesOffset = 0
    private val handle = let {
        val status = IntArray(1)
        glGenBuffers(1, status, 0)
        status[0]
    }
    private val byteBuffer = ByteBuffer.allocateDirect(maxIndices * INDEX_BYTE_LENGTH).apply {
        order(ByteOrder.nativeOrder())
    }
    private val intBuffer = byteBuffer.asIntBuffer().apply {
        clear()
        while (position() < capacity()) put(0)
        rewind()
    }

    /**
     * Bind this index buffer to the GL_ELEMENT_ARRAY_BUFFER target.
     */
    fun bind() {
        assertFalse("Index buffer is currently recording", recording)
        intBuffer.rewind()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handle)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, maxIndices * INDEX_BYTE_LENGTH, intBuffer,
                GL_STATIC_DRAW)
    }

    /**
     * Start recording new indices. Any previously hold data is discarded.
     */
    fun startRecording() {
        assertFalse("Index buffer is already recording", recording)
        globalIndicesOffset = 0
        globalIndex = 0
        globalIndexCounter = 0
        recording = true
    }

    /**
     * End recording, allowing this buffer to be bound and drawn.
     * You cannot add new indices unless you re-record the buffer using startRecording().
     */
    fun endRecording() {
        assertTrue("Index buffer is not recording", recording)
        recording = false
    }

    /**
     * Add a [geometry] to this index buffer.
     * @return Index offset for this geometry.
     */
    fun recordGeometry(geometry: Geometry): Int {
        assertTrue("Index buffer is not recording", recording)

        for ((first, second) in geometry.lines) {
            appendIndex(first)
            appendIndex(second)
        }

        val ret = globalIndexCounter
        globalIndexCounter += geometry.lines.size * 2
        globalIndicesOffset += geometry.points.size

        return ret
    }

    private fun appendIndex(index: Int) {
        assertTrue("Insufficient memory to store vertex: pos=%d  cap=%d".format(
                intBuffer.position(), intBuffer.capacity()),
                intBuffer.position() < intBuffer.capacity())
        intBuffer.put(globalIndicesOffset + index)
    }

}
