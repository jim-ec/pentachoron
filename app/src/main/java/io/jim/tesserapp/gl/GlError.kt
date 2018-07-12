package io.jim.tesserapp.gl

import android.opengl.GLES20

/**
 * Thrown upon OpenGL-calls related errors.
 */
class GlError(msg: String, error: Int = GLES20.glGetError()) :
        RuntimeException("OpenGL Error 0x$error (" + when (error) {
            GLES20.GL_NO_ERROR -> "no error"
            GLES20.GL_INVALID_ENUM -> "invalid enumeration"
            GLES20.GL_INVALID_VALUE -> "invalid value"
            GLES20.GL_INVALID_OPERATION -> "invalid operation"
            GLES20.GL_INVALID_FRAMEBUFFER_OPERATION -> "invalid framebuffer operation"
            GLES20.GL_OUT_OF_MEMORY -> "out of memory"
            else -> "unknown error"
        } + "): " + msg) {
    
    companion object {
        
        /**
         * Simply checks for an error-state and throws [GlError] if so.
         * @param currentAction Short description what the caller is currently about to do.
         */
        fun check(currentAction: String) {
            val error = GLES20.glGetError()
            if (error != GLES20.GL_NO_ERROR) {
                throw GlError("Occurred when: $currentAction", error)
            }
        }
        
    }
    
}
