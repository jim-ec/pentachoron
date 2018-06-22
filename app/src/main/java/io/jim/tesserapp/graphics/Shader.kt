package io.jim.tesserapp.graphics

import android.content.res.AssetManager
import android.opengl.GLES30
import io.jim.tesserapp.graphics.engine.GlException
import io.jim.tesserapp.graphics.engine.GlProgram
import io.jim.tesserapp.graphics.engine.GlTransformFeedback
import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.util.allocateNativeFloatMemory

/**
 * A shader pipeline with a vertex shader, fragment shader and locations of all attributes and
 * uniforms.
 */
class Shader(assets: AssetManager) {

    val program = GlProgram(
            assets = assets,
            vertexShaderFile = "lines.vert",
            fragmentShaderFile = "lines.frag",
            transformFeedback = GlTransformFeedback("gl_Position", GLES30.GL_LINES))

    /**
     * GLSL location of position attribute.
     */
    val positionAttributeLocation = GLES30.glGetAttribLocation(program.handle, "position")

    /**
     * GLSL location of color attribute.
     */
    val colorAttributeLocation = GLES30.glGetAttribLocation(program.handle, "color")

    private val viewMatrixLocation = GLES30.glGetUniformLocation(program.handle, "V")
    private val projectionMatrixLocation = GLES30.glGetUniformLocation(program.handle, "P")

    private val floatBuffer = allocateNativeFloatMemory(16).asFloatBuffer()

    private fun writeMatrixIntoFloatBuffer(matrix: Matrix) {
        matrix.forEachComponent { row, col ->
            if (row <= 3 && col <= 3)
                floatBuffer.put(row * 4 + col, matrix[row, col].toFloat())
        }
    }

    /**
     * Upload [matrix] to the view matrix uniform.
     */
    fun uploadViewMatrix(matrix: Matrix) {
        writeMatrixIntoFloatBuffer(matrix)
        GLES30.glUniformMatrix4fv(viewMatrixLocation, 1, false, floatBuffer)
        GlException.check("Uploading view matrix")
    }

    /**
     * Upload [matrix] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(matrix: Matrix) {
        writeMatrixIntoFloatBuffer(matrix)
        GLES30.glUniformMatrix4fv(projectionMatrixLocation, 1, false, floatBuffer)
        GlException.check("Uploading projection matrix")
    }

}
