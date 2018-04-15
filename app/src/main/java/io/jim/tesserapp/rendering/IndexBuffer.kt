package io.jim.tesserapp.rendering

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

    /**
     * The count of currently recorded indices.
     */
    var indexCounter = 0
        private set

    private var vertexCounter = 0
    private var recording = false
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

    companion object {
        private const val INDEX_BYTE_LENGTH = 4
    }

    /**
     * Bind this index buffer to the GL_ELEMENT_ARRAY_BUFFER target.
     */
    fun bind() {
        assertFalse("Index array is currently recording", recording)
        intBuffer.rewind()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handle)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, maxIndices * INDEX_BYTE_LENGTH, intBuffer,
                GL_STATIC_DRAW)
    }

    /**
     * Start recording new indices. Any previously hold data is discarded.
     */
    fun startRecording() {
        assertFalse("Index array is already recording", recording)
        vertexCounter = 0
        indexCounter = 0
        recording = true
    }

    /**
     * End recording, allowing this buffer to be bound and drawn.
     * You cannot add new indices unless you re-record the buffer using startRecording().
     */
    fun endRecording() {
        assertTrue("Index array is not recording", recording)
        recording = false
    }

    /**
     * Add a [geometry] to this index buffer.
     */
    fun recordGeometry(geometry: Geometry) {
        assertTrue("Index array is not recording", recording)

        for ((first, second) in geometry.lines) {
            appendIndex(first)
            appendIndex(second)
        }

        // Increase the global counter by the number of indices this geometry consumed:
        indexCounter += geometry.lines.size * 2

        // Increase the vertex counter by the number of vertices this geometry occupied.
        // That's necessary since the next geometry's zeroth vertex actually lies
        // after all vertices recorded at that time-point.
        vertexCounter += geometry.points.size
    }

    /**
     * Append a new index. [index] is a geometry-local index, which is converted into a global
     * index automatically. That depends on the count of vertices recorded at this time-point.
     */
    private fun appendIndex(index: Int) {
        assertTrue("Insufficient localMemory to store vertex: pos=%d  cap=%d".format(
                intBuffer.position(), intBuffer.capacity()),
                intBuffer.position() < intBuffer.capacity())
        intBuffer.put(vertexCounter + index)
    }

}
