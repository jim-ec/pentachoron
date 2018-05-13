package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30

/**
 * Provide a VAO.
 */
abstract class GlVertexBuffer {

    /**
     * Store vertex attribute pointers.
     */
    val vertexArray = resultCode { GLES30.glGenVertexArrays(1, resultCode) }

    /**
     * Invokes [f] while the internal VAO is being bound.
     * Do not bind any other VAO during this call.
     */
    fun vertexArrayBound(f: () -> Unit) {
        GLES30.glBindVertexArray(vertexArray)
        f()
        GLES30.glBindVertexArray(0)
    }

    /**
     * Upload the buffer contents to the GPU.
     */
    abstract fun write()

}
