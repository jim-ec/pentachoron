package io.jim.tesserapp.rendering.engine

import android.opengl.GLES20
import android.opengl.GLES30

open class GlProgram(
        vertexShaderSource: String,
        fragmentShaderSource: String
) {

    val programHandle = GLES20.glCreateProgram()
    val vertexShader = GlShader(GLES30.GL_VERTEX_SHADER, vertexShaderSource)
    val fragmentShader = GlShader(GLES30.GL_FRAGMENT_SHADER, fragmentShaderSource)

    init {
        GLES30.glAttachShader(programHandle, vertexShader.shaderHandle)
        GLES30.glAttachShader(programHandle, fragmentShader.shaderHandle)

        // Link program together:
        GLES30.glLinkProgram(programHandle)
        GLES30.glGetProgramiv(programHandle, GLES30.GL_LINK_STATUS, resultCode)
        if (GLES30.GL_TRUE != resultCode()) {
            throw GlException("Cannot link program: ${GLES30.glGetProgramInfoLog(programHandle)}")
        }

        // Validate program:
        GLES30.glValidateProgram(programHandle)
        GLES30.glGetProgramiv(programHandle, GLES30.GL_VALIDATE_STATUS, resultCode)
        if (GLES30.GL_TRUE != resultCode()) {
            throw GlException("Cannot validate program: ${GLES30.glGetProgramInfoLog(programHandle)}")
        }

        GlException.check("Program initialization")
    }

    /**
     * Use the program for further draw calls.
     */
    fun bind() {
        GLES30.glUseProgram(programHandle)
    }

}
