package io.jim.tesserapp.graphics.gl

import android.content.res.AssetManager
import android.opengl.GLES20

/**
 * Encapsulate a GL program, containing a vertex and fragment shader.
 *
 * @param assets Asset manager used to open shader source files.
 * @param vertexShaderFile File name of vertex shader source code.
 * @param fragmentShaderFile File name of fragment shader source code.
 */
class Program(
        assets: AssetManager,
        vertexShaderFile: String,
        fragmentShaderFile: String
) {
    
    /**
     * This program's vertex shader.
     */
    private val vertexShader = Shader(assets, vertexShaderFile)
    
    /**
     * This program's fragment shader.
     */
    private val fragmentShader = Shader(assets, fragmentShaderFile)
    
    /**
     * Actual program handle retrieved from GL.
     */
    val handle = GLES20.glCreateProgram()
    
    init {
        GLES20.glAttachShader(handle, vertexShader.handle)
        GLES20.glAttachShader(handle, fragmentShader.handle)
        
        // Link program together:
        GLES20.glLinkProgram(handle)
        GLES20.glGetProgramiv(handle, GLES20.GL_LINK_STATUS, resultCode)
        if (GLES20.GL_TRUE != resultCode()) {
            throw GlError("Cannot link program: ${GLES20.glGetProgramInfoLog(handle)}")
        }
        
        // Validate program:
        GLES20.glValidateProgram(handle)
        GLES20.glGetProgramiv(handle, GLES20.GL_VALIDATE_STATUS, resultCode)
        if (GLES20.GL_TRUE != resultCode()) {
            throw GlError("Cannot validate program: ${GLES20.glGetProgramInfoLog(handle)}")
        }
        
        GlError.check("Program initialization")
    }
    
    /**
     * Use this program for further draw calls.
     *
     * @throws RuntimeException If another program is currently in use.
     */
    inline fun bound(crossinline f: () -> Unit) {
        if (0 != resultCode { GLES20.glGetIntegerv(GLES20.GL_CURRENT_PROGRAM, resultCode) })
            throw RuntimeException("Another program is currently used.")
        
        GLES20.glUseProgram(handle)
        
        f()
        
        GLES20.glUseProgram(0)
    }
    
}
