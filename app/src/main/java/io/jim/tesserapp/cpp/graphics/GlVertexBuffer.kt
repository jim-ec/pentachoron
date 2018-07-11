package io.jim.tesserapp.cpp.graphics

import android.opengl.GLES20

/**
 * GL VAO.
 *
 * @property drawMode Mode how to draw the vertices, e.g. [GLES20.GL_TRIANGLE_STRIP].
 */
class GlVertexBuffer(
        val drawMode: Int,
        val instructVertexAttributePointers: () -> Unit
) {
    
    val buffer = GlBuffer(GLES20.GL_ARRAY_BUFFER, GLES20.GL_STATIC_DRAW)
    
}
