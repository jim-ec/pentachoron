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

    private data class GeometryEntry(val geometry: Geometry, val indexOffset: Int, val indexCount: Int)

    private var recording = false
    private var globalIndex = 0
    private var globalIndexCounter = 0
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

    /**
     * Bind this index buffer to the GL_ELEMENT_ARRAY_BUFFER target.
     */
    fun bind() {
        assertFalse("Index buffer is currently recording", recording)
        intBuffer.rewind()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, handle)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, size * INDEX_BYTE_LENGTH, intBuffer, GL_STATIC_DRAW)
    }

    /**
     * Start recording new indices. Any previously hold data is discarded.
     */
    fun startRecording() {
        assertFalse("Index buffer is already recording", recording)
        geometryRegistry.clear()
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
     * The actual vertex data is added to the [vertexBuffer].
     */
    fun recordGeometry(geometry: Geometry, vertexBuffer: VertexBuffer) {
        assertTrue("Index buffer is not recording", recording)

        for (point in geometry.points) {
            assertEquals("All vertices must be 3D", 3, point.dimension)
            vertexBuffer.appendVertex(point, geometry.color)
        }

        for (line in geometry.lines) {
            appendIndex(line.first)
            appendIndex(line.second)
        }

        geometryRegistry.add(GeometryEntry(geometry, globalIndexCounter, geometry.lines.size * 2))
        globalIndexCounter += geometry.lines.size * 2
        globalIndicesOffset += geometry.points.size
    }

    /**
     * Call a function for each stored geometry.
     * The function [f] gets the following parameters:
     *  - indexOffset: The position within this index buffer the current geometry's indices begin.
     *  - indexCount: The count of the current geometry's indices.
     */
    fun forEachGeometry(f: (geometry: Geometry, indexOffset: Int, indexCount: Int) -> Unit) {
        geometryRegistry.forEach { entry ->
            f(entry.geometry, entry.indexOffset, entry.indexCount)
        }
    }

    private fun appendIndex(index: Int) {
        assertTrue("Insufficient memory to store vertex: pos=%d  cap=%d".format(intBuffer.position(), intBuffer.capacity()),
                intBuffer.position() < intBuffer.capacity())
        intBuffer.put(globalIndicesOffset + index)
    }

}