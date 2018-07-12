package io.jim.tesserapp.graphics

import android.content.res.AssetManager
import android.opengl.GLES20

/**
 * A shader pipeline with a vertex shader, fragment shader and locations of all attributes and
 * uniforms.
 */
class Shader(assets: AssetManager) {
    
    val program = GlProgram(
            assets = assets,
            vertexShaderFile = "lines.vert",
            fragmentShaderFile = "lines.frag")
    
    /**
     * GLSL location of position attribute.
     */
    val positionAttributeLocation = GLES20.glGetAttribLocation(program.handle, "position")
    
    /**
     * GLSL location of color attribute.
     */
    val colorAttributeLocation = GLES20.glGetAttribLocation(program.handle, "color")
    
    val viewMatrixLocation = GLES20.glGetUniformLocation(program.handle, "V")
    
    val projectionMatrixLocation = GLES20.glGetUniformLocation(program.handle, "P")
    
}
