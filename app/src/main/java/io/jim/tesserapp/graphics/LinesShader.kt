package io.jim.tesserapp.graphics

import android.content.res.AssetManager
import android.opengl.GLES20
import io.jim.tesserapp.gl.ATTRIBUTE_FLOATS
import io.jim.tesserapp.gl.FLOAT_BYTE_LENGTH
import io.jim.tesserapp.gl.Program

/**
 * A shader pipeline with a vertex shader, fragment shader and locations of all attributes and
 * uniforms.
 */
class LinesShader(assets: AssetManager) {
    
    val program = Program(
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
    
    companion object {
        
        
        /**
         * Counts of different attributes.
         * - Position
         * - Color
         */
        const val ATTRIBUTE_COUNTS = 2
        
        /**
         * Count of floats each vertex consists of.
         */
        const val VERTEX_FLOATS = ATTRIBUTE_COUNTS * ATTRIBUTE_FLOATS
        
        /**
         * Count of bytes each vertex consists of.
         */
        const val VERTEX_STRIDE = VERTEX_FLOATS * FLOAT_BYTE_LENGTH
        
        /**
         * Position attribute offset, in bytes.
         */
        const val VERTEX_OFFSET_POSITION = 0
        
        /**
         * Color attribute offset, in bytes.
         */
        const val VERTEX_OFFSET_COLOR = VERTEX_OFFSET_POSITION + ATTRIBUTE_FLOATS * FLOAT_BYTE_LENGTH
        
    }
    
}
