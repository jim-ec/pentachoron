package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.math.transform.Matrix
import io.jim.tesserapp.rendering.engine.GlException
import io.jim.tesserapp.rendering.engine.GlProgram
import io.jim.tesserapp.rendering.engine.GlTransformFeedback
import java.nio.FloatBuffer

/**
 * A shader pipeline with a vertex shader, fragment shader an locations of all attributes and
 * uniforms.
 */
class Shader : GlProgram(
        vertexShaderSource,
        fragmentShaderSource,
        GlTransformFeedback("gl_Position", GLES30.GL_LINES)) {

    /**
     * GLSL location of position attribute.
     */
    val positionAttributeLocation = GLES30.glGetAttribLocation(programHandle, "position")

    /**
     * GLSL location of color attribute.
     */
    val colorAttributeLocation = GLES30.glGetAttribLocation(programHandle, "color")

    /**
     * GLSL location of model matrix index attribute.
     * Although it's an index into the model matrix array, it is represented as a float.
     */
    val modelIndexAttributeLocation = GLES30.glGetAttribLocation(programHandle, "modelIndex")

    private val viewMatrixLocation = GLES30.glGetUniformLocation(programHandle, "V")
    private val projectionMatrixLocation = GLES30.glGetUniformLocation(programHandle, "P")
    private val modelMatrixLocation = GLES30.glGetUniformLocation(programHandle, "M")

    companion object {

        private val vertexShaderSource = """
            #version 300 es

            uniform mat4 P;
            uniform mat4 V;
            uniform mat4 M[100];

            in vec4 position;
            in vec4 color;
            in int modelIndex;

            out vec4 vColor;

            void main() {
                gl_Position = P * V * M[modelIndex] * position;
                vColor = color;
            }

            """.trim()

        private val fragmentShaderSource = """
            #version 300 es

            in mediump vec4 vColor;

            out mediump vec4 fColor;

            void main() {
                fColor = vColor;
            }

            """.trim()

    }

    /**
     * Upload matrices from [floatMemory] to GL.
     *
     * @param uploadCounts Counts of model matrices to be uploaded.
     *
     * @throws RuntimeException
     * If [floatMemory]'s [FloatBuffer.position] is not 0.
     * This is not actually an error, but a strong indicator for a bug,
     * since buffers need to rewind their positions before using [GLES30.glUniformMatrix4fv].
     */
    fun uploadModelMatrices(floatMemory: FloatBuffer, uploadCounts: Int) {
        if (floatMemory.position() != 0)
            throw RuntimeException("Float memory position must be 0 to upload to GL")

        GLES30.glUniformMatrix4fv(modelMatrixLocation, uploadCounts, false, floatMemory)
        GlException.check("Uploading model matrices")
    }

    /**
     * Upload [matrix] to the view matrix uniform.
     */
    fun uploadViewMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(viewMatrixLocation, 1, false, matrix.floats)
        GlException.check("Uploading view matrix")
    }

    /**
     * Upload [matrix] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(projectionMatrixLocation, 1, false, matrix.floats)
        GlException.check("Uploading projection matrix")
    }

}
