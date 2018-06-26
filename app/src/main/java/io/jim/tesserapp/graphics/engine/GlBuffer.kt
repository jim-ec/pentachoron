package io.jim.tesserapp.graphics.engine

import android.opengl.GLES20
import io.jim.tesserapp.geometry.FLOAT_BYTE_LENGTH

/**
 * GL Buffer.
 *
 * Does not actually store memory, since e.g. reading causes a buffer to be created through
 * mapping.
 *
 * @property target The GL target this buffer is bound to, e.g. [GLES20.GL_ARRAY_BUFFER].
 * @property usage How this buffer is intended to be used, see [GLES20.glBufferData].
 */
class GlBuffer(
        val target: Int,
        private val usage: Int
) {
    
    /**
     * The actual handle retrieved from GL.
     */
    val handle = resultCode { GLES20.glGenBuffers(1, resultCode) }
    
    /**
     * Maps GL targets to their binding-query constant counterparts.
     */
    inline val binding: Int
        get() = when (target) {
            GLES20.GL_ARRAY_BUFFER -> GLES20.GL_ARRAY_BUFFER_BINDING
            GLES20.GL_ELEMENT_ARRAY_BUFFER -> GLES20.GL_ELEMENT_ARRAY_BUFFER_BINDING
            else -> throw RuntimeException("Unknown binding known for target $target")
        }
    
    /**
     * Allocate memory and optionally write data in.
     * @param vectorCapacity Counts of 4d vectors to be written.
     * @param data Data to be written.
     */
    fun allocate(vectorCapacity: Int, data: java.nio.Buffer? = null) {
        bound {
            GLES20.glBufferData(
                    target,
                    vectorCapacity * 4 * FLOAT_BYTE_LENGTH,
                    data,
                    usage
            )
            GlException.check("Allocate memory memory")
        }
    }
    
    /**
     * Calls [f] while this buffer is bound to its [target].
     */
    inline fun bound(crossinline f: () -> Unit) {
        val oldBinding = resultCode { GLES20.glGetIntegerv(binding, resultCode) }
        GLES20.glBindBuffer(target, handle)
        f()
        GLES20.glBindBuffer(target, oldBinding)
    }
    
}
