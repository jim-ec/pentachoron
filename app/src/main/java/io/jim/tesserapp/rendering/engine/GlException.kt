package io.jim.tesserapp.rendering.engine

import android.opengl.GLES30

/**
 * Thrown upon OpenGL-calls related errors.
 */
class GlException(msg: String, error: Int = GLES30.glGetError()) :
        RuntimeException("OpenGL Error 0x$error (" + when (error) {
            GLES30.GL_NO_ERROR -> "no error"
            GLES30.GL_INVALID_ENUM -> "invalid enumeration"
            GLES30.GL_INVALID_VALUE -> "invalid value"
            GLES30.GL_INVALID_OPERATION -> "invalid operation"
            GLES30.GL_INVALID_FRAMEBUFFER_OPERATION -> "invalid framebuffer operation"
            GLES30.GL_OUT_OF_MEMORY -> "out of memory"
            else -> "unknown error"
        } + "): " + msg)
