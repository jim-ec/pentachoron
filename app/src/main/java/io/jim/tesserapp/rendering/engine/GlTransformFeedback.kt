package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30

class GlTransformFeedback(val varying: String)
    : GlBuffer(GLES30.GL_ARRAY_BUFFER) {

    fun setup(programHandle: Int) {
        GLES30.glTransformFeedbackVaryings(
                programHandle,
                arrayOf(varying),
                GLES30.GL_INTERLEAVED_ATTRIBS
        )
    }

    /**
     * Enables transform feedback capturing into this buffer as long as [f] runs.
     * **Don't forget to [allocate] the buffer before capturing transform feedback.**
     * After that, you can read out feedback using [read].
     *
     * @param mode Transform feedback mode, like [GLES30.GL_TRIANGLES].
     */
    inline fun capturingTransformFeedback(mode: Int, f: () -> Unit) {
        GLES30.glBindBufferBase(GLES30.GL_TRANSFORM_FEEDBACK_BUFFER, 0, bufferHandle)
        GLES30.glBeginTransformFeedback(mode)
        GlException.check("Begin transform feedback")

        f()

        GLES30.glEndTransformFeedback()
        GlException.check("End transform feedback")

        GLES30.glFlush()
    }

}
