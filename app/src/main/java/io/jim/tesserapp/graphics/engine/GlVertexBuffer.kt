package io.jim.tesserapp.graphics.engine

import android.opengl.GLES30
import io.jim.tesserapp.util.InputStreamMemory

/**
 * GL VAO.
 *
 * @property drawMode Mode how to draw the vertices, e.g. [GLES30.GL_TRIANGLE_STRIP].
 */
class GlVertexBuffer(private val drawMode: Int) {
    
    /**
     * Store vertex attribute pointers.
     */
    val vertexArray = resultCode { GLES30.glGenVertexArrays(1, resultCode) }

    val buffer = GlBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_STATIC_DRAW)
    
    /**
     * Upload data from [memory] to GL.
     */
    fun upload(memory: InputStreamMemory) {
        buffer.allocate(
                memory.writtenVectorCounts,
                memory.floatMemory
        )
    }
    
    /**
     * Draw vertex data with [drawMode].
     *
     * @param elementCounts
     * Count of element to be drawn.
     * This count is given in units of elements, *not vertices*.
     *
     * @throws GlException If drawing failed.
     */
    fun draw(elementCounts: Int) {
        bound {
            GLES30.glDrawArrays(
                    drawMode,
                    0,
                    elementCounts)
            GlException.check("Draw vertex memory")
        }
    }

    /**
     * Call [f] while the VBO and VAO are bound.
     */
    inline fun bound(crossinline f: () -> Unit) {
        if (0 != resultCode { GLES30.glGetIntegerv(GLES30.GL_VERTEX_ARRAY_BINDING, resultCode) })
            throw RuntimeException("Cannot bind VAO, another one is currently bound")

        GLES30.glBindVertexArray(vertexArray)

        buffer.bound(f)

        GLES30.glBindVertexArray(0)
    }
    
}
