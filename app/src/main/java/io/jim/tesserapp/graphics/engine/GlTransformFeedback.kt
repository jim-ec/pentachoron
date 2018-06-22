package io.jim.tesserapp.graphics.engine

import android.opengl.GLES30
import java.nio.Buffer

/**
 * Captures transform feedback when drawing with a [GlProgram].
 *
 * @property varying Varying to be captured.
 * @property mode Transform feedback mode, e.g. [GLES30.GL_TRIANGLES].
 */
class GlTransformFeedback(private val varying: String, val mode: Int) {

    val buffer = GlBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_STATIC_READ)
    
    /**
     * Indicates whether the transform feedback object has already allocated its memory.
     * If not, no capturing occurs.
     */
    var allocated = false
        private set
    
    //var lastDump = 0L
    
    /**
     * Setup the feedback capturing information.
     * Is automatically called by [GlProgram] when creating its program instance.
     */
    internal fun setup(programHandle: Int) {
        GLES30.glTransformFeedbackVaryings(
                programHandle,
                arrayOf(varying),
                GLES30.GL_INTERLEAVED_ATTRIBS
        )
    }

    fun allocate(vectorCapacity: Int, data: Buffer? = null) {
        buffer.allocate(vectorCapacity, data)
        allocated = true
    }
    
    /**
     * Enables transform feedback capturing into this buffer as long as [f] runs.
     * After that, you can read out feedback using [GlBuffer.read].
     */
    inline fun capturingTransformFeedback(crossinline f: () -> Unit) {
        if (allocated) {
            GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, buffer.bufferHandle)
            GLES30.glBeginTransformFeedback(mode)
            GlException.check("Begin transform feedback")
            
            f()
            
            GLES30.glEndTransformFeedback()
            GlException.check("End transform feedback")
            
            GLES30.glFlush()
            
            /*if (System.currentTimeMillis() - lastDump > 1000) {
                lastDump = System.currentTimeMillis()
                println("-----------------------------------------------------------------")
                read(1) { vectors, index ->
                    vectors[0] /= vectors[0].q
                    println("TF[$index]: $varying=${vectors[0]}")
                }
            }*/
        } else {
            f()
        }
        
    }
    
}
