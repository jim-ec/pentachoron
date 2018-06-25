package io.jim.tesserapp.graphics

import android.content.res.AssetManager
import android.opengl.GLES30
import io.jim.tesserapp.graphics.engine.GlException
import io.jim.tesserapp.graphics.engine.GlProgram
import io.jim.tesserapp.math.matrix.Matrix

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
    val positionAttributeLocation = GLES30.glGetAttribLocation(program.handle, "position")
    
    /**
     * GLSL location of color attribute.
     */
    val colorAttributeLocation = GLES30.glGetAttribLocation(program.handle, "color")
    
    val viewMatrixLocation = GLES30.glGetUniformLocation(program.handle, "V")
    
    val projectionMatrixLocation = GLES30.glGetUniformLocation(program.handle, "P")
    
    /**
     * Upload [matrix] to the view matrix uniform.
     */
    fun uploadViewMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(viewMatrixLocation, 1, false, matrix.toFloatArray(), 0)
        GlException.check("Uploading view matrix")
    }
    
    /**
     * Upload [matrix] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(projectionMatrixLocation, 1, false, matrix.toFloatArray(), 0)
        GlException.check("Uploading projection matrix")
    }
    
}
