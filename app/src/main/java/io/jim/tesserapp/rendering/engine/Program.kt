package io.jim.tesserapp.rendering.engine

import android.opengl.GLES20
import android.opengl.GLES30
import io.jim.tesserapp.rendering.resultCode

class Program(
        vertexShaderSource: String,
        fragmentShaderSource: String
) {

    val handle = GLES20.glCreateProgram()
    val vertexShader = Shader(GLES30.GL_VERTEX_SHADER, vertexShaderSource)
    val fragmentShader = Shader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderSource)

    init {
        GLES30.glAttachShader(handle, vertexShader.handle)
        GLES30.glAttachShader(handle, fragmentShader.handle)

        // Link program together:
        GLES30.glLinkProgram(handle)
        GLES30.glGetProgramiv(handle, GLES30.GL_LINK_STATUS, resultCode)
        if (GLES30.GL_TRUE != resultCode()) {
            throw GlException("Cannot link program: ${GLES30.glGetProgramInfoLog(handle)}")
        }

        // Validate program:
        GLES30.glValidateProgram(handle)
        GLES30.glGetProgramiv(handle, GLES30.GL_VALIDATE_STATUS, resultCode)
        if (GLES30.GL_TRUE != resultCode()) {
            throw GlException("Cannot validate program: ${GLES30.glGetProgramInfoLog(handle)}")
        }

    }

    /**
     * Use the program for further draw calls.
     */
    fun bind() {
        GLES30.glUseProgram(handle)
    }

}
