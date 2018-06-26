package io.jim.tesserapp.cpp.graphics

import android.content.res.AssetManager
import android.opengl.GLES20
import io.jim.tesserapp.cpp.matrix.Matrix

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
    
    /**
     * Upload [matrix] to the view matrix uniform.
     */
    fun uploadViewMatrix(matrix: Matrix) {
        GLES20.glUniformMatrix4fv(viewMatrixLocation, 1, false, matrix.toFloatArray(), 0)
        GlException.check("Uploading view matrix")
    }
    
    /**
     * Upload [matrix] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(matrix: Matrix) {
        GLES20.glUniformMatrix4fv(projectionMatrixLocation, 1, false, matrix.toFloatArray(), 0)
        GlException.check("Uploading projection matrix")
    }
    
}
