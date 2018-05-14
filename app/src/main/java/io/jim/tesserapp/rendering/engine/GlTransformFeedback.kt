package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30
import java.nio.Buffer

/**
 * @property mode Transform feedback mode, like [GLES30.GL_TRIANGLES].
 */
class GlTransformFeedback(val varying: String, val mode: Int)
    : GlBuffer(GLES30.GL_ARRAY_BUFFER, GLES30.GL_STATIC_READ) {

    var allocated = false
        private set

    var lastDump = 0L

    fun setup(programHandle: Int) {
        GLES30.glTransformFeedbackVaryings(
                programHandle,
                arrayOf(varying),
                GLES30.GL_INTERLEAVED_ATTRIBS
        )
    }

    override fun allocate(vectorCapacity: Int, data: Buffer?) {
        super.allocate(vectorCapacity, data)
        allocated = true
    }

    /**
     * Enables transform feedback capturing into this buffer as long as [f] runs.
     * After that, you can read out feedback using [read].
     */
    inline fun capturingTransformFeedback(f: () -> Unit) {
        if (allocated) {
            GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, bufferHandle)
            GLES30.glBeginTransformFeedback(mode)
            GlException.check("Begin transform feedback")

            f()

            GLES30.glEndTransformFeedback()
            GlException.check("End transform feedback")

            GLES30.glFlush()

            if (System.currentTimeMillis() - lastDump > 1000) {
                lastDump = System.currentTimeMillis()
                println("-----------------------------------------------------------------")
                read(1) { vectors, index ->
                    vectors[0] /= vectors[0].q
                    println("TF[$index]: $varying=${vectors[0]}")
                }
            }
        }
        else {
            f()
        }

    }

}
