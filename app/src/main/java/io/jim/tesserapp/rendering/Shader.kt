package io.jim.tesserapp.rendering

import android.opengl.GLES30
import io.jim.tesserapp.math.transform.Matrix
import io.jim.tesserapp.rendering.engine.GlException
import io.jim.tesserapp.rendering.engine.GlProgram
import io.jim.tesserapp.util.RandomAccessBuffer

/**
 * A shader pipeline with a vertex shader, fragment shader an locations of all attributes and
 * uniforms.
 */
class Shader : GlProgram(vertexShaderSource, fragmentShaderSource) {

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

            in vec3 position;
            in vec3 color;
            in float modelIndex;

            out vec3 vColor;

            void main() {
                gl_Position = P * V * M[int(modelIndex)] * vec4(position, 1.0);
                vColor = color;
            }

            """.trim()

        private val fragmentShaderSource = """
            #version 300 es

            in mediump vec3 vColor;

            out mediump vec4 fColor;

            void main() {
                fColor = vec4(vColor, 1.0);
            }

            """.trim()

    }

    /**
     * Upload the first [uploadCounts] matrices from [buffer] into the model matrix uniform array.
     */
    fun uploadModelMatrixBuffer(buffer: RandomAccessBuffer, uploadCounts: Int) {
        GLES30.glUniformMatrix4fv(modelMatrixLocation, uploadCounts, false, buffer.floatBuffer)
        GlException.check("Uploading model buffer")
    }

    /**
     * Upload [matrix] to the view matrix uniform.
     */
    fun uploadViewMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(viewMatrixLocation, 1, false, matrix.floats.floatBuffer)
        GlException.check("Uploading view matrix")
    }

    /**
     * Upload [matrix] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(matrix: Matrix) {
        GLES30.glUniformMatrix4fv(projectionMatrixLocation, 1, false, matrix.floats.floatBuffer)
        GlException.check("Uploading projection matrix")
    }

}
