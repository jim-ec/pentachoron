package io.jim.tesserapp.graphics

import android.content.res.AssetManager
import android.opengl.GLES20
import io.jim.tesserapp.gl.Program

/**
 * Shader responsible for drawing lines.
 */
class LinesShader(assets: AssetManager) {
    
    val program = Program(
            assets = assets,
            vertexShaderFile = "lines.vert",
            fragmentShaderFile = "lines.frag")
    
    /**
     * Position attribute location.
     */
    val positionAttributeLocation = GLES20.glGetAttribLocation(program.handle, "position")
    
    /**
     * Color attribute location.
     */
    val colorAttributeLocation = GLES20.glGetAttribLocation(program.handle, "color")
    
    /**
     * View matrix uniform location.
     */
    val viewMatrixLocation = GLES20.glGetUniformLocation(program.handle, "V")
    
    /**
     * Projection matrix uniform location.
     */
    val projectionMatrixLocation = GLES20.glGetUniformLocation(program.handle, "P")
    
}