package io.jim.tesserapp.graphics.engine

import android.opengl.GLES30

class GlVertexArray {
    
    val handle = resultCode { GLES30.glGenVertexArrays(1, resultCode) }
    
    /**
     * Call [f] while the VAO is bound.
     */
    inline fun bound(crossinline f: () -> Unit) {
        if (0 != resultCode { GLES30.glGetIntegerv(GLES30.GL_VERTEX_ARRAY_BINDING, resultCode) })
            throw RuntimeException("Cannot bind VAO, another one is currently bound")
        
        GLES30.glBindVertexArray(handle)
        
        f()
        
        GLES30.glBindVertexArray(0)
    }
    
}
