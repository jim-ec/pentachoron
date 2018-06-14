package io.jim.tesserapp.graphics.engine

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLES30

/**
 * Encapsulate a GL program, containing a vertex and fragment shader.
 *
 * @param assets Asset manager used to open shader source files.
 * @param vertexShaderFile File name of vertex shader source code.
 * @param fragmentShaderFile File name of fragment shader source code.
 *
 * @property transformFeedback
 * Transform feedback used with this program.
 * Its [GlTransformFeedback.setup] function is called after attaching both shaders and
 * before linking the actual program.
 * If a transform feedback object is present, drawing happens inside its
 * [GlTransformFeedback.capturingTransformFeedback] block.
 */
open class GlProgram protected constructor(
        assets: AssetManager,
        vertexShaderFile: String,
        fragmentShaderFile: String,
        val transformFeedback: GlTransformFeedback? = null
) {

    /**
     * This program's vertex shader.
     */
    private val vertexShader = GlShader(assets, vertexShaderFile)

    /**
     * This program's fragment shader.
     */
    private val fragmentShader = GlShader(assets, fragmentShaderFile)

    /**
     * Actual program handle retrieved from GL.
     */
    val programHandle = GLES20.glCreateProgram()

    init {
        GLES30.glAttachShader(programHandle, vertexShader.shaderHandle)
        GLES30.glAttachShader(programHandle, fragmentShader.shaderHandle)

        transformFeedback?.setup(programHandle)
        GlException.check("Setting up transform feedback")

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
     * Use this program for further draw calls.
     *
     * @throws RuntimeException If another program is currently in use.
     */
    inline fun bound(crossinline f: () -> Unit) {
        if (0 != resultCode { GLES30.glGetIntegerv(GLES30.GL_CURRENT_PROGRAM, resultCode) })
            throw RuntimeException("Another program is currently used.")

        GLES30.glUseProgram(programHandle)

        // If a transform feedback is attached, do drawing while capture feedback.
        // Otherwise, just do the draw code:
        transformFeedback?.capturingTransformFeedback { f() }
                ?: f()

        GLES30.glUseProgram(0)
    }

}
