package io.jim.tesserapp.rendering

import android.opengl.GLES20.*
import io.jim.tesserapp.math.MatrixBuffer
import junit.framework.Assert.assertTrue

/**
 * A shader pipeline with a vertex shader, fragment shader an locations of all attributes and
 * uniforms.
 */
class Shader(maxModels: Int) {

    /**
     * GLSL location of position attribute.
     */
    val positionAttributeLocation: Int

    /**
     * GLSL location of color attribute.
     */
    val colorAttributeLocation: Int

    /**
     * GLSL location of model matrix index attribute.
     * Although it's an index into the model matrix array, it is represented as a float.
     */
    val modelIndexAttributeLocation: Int

    private val vertexShader = glCreateShader(GL_VERTEX_SHADER)
    private val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
    private val program = glCreateProgram()
    private val modelMatrixLocation: Int
    private val viewMatrixLocation: Int
    private val projectionMatrixLocation: Int

    private val vertexShaderSource = """
            uniform mat4 P;
            uniform mat4 V;
            uniform mat4 M[$maxModels];

            attribute vec3 position;
            attribute vec3 color;
            attribute float modelIndex;

            varying vec3 vColor;

            void main() {
                gl_Position = P * V * M[int(modelIndex)] * vec4(position, 1.0);
                vColor = color;
            }
        """

    private val fragmentShaderSource = """
            varying mediump vec3 vColor;

            void main() {
                gl_FragColor = vec4(vColor, 1.0);
            }
        """

    init {
        assertTrue(vertexShader >= 0)
        assertTrue(fragmentShader >= 0)
        assertTrue(program >= 0)

        val status = IntArray(1)

        vertexShader.also {
            glShaderSource(it, vertexShaderSource)
            glCompileShader(it)
            glGetShaderiv(it, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot compile vertex shader: ${glGetShaderInfoLog(it)}")
            }
        }

        fragmentShader.also {
            glShaderSource(it, fragmentShaderSource)
            glCompileShader(it)
            glGetShaderiv(it, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot compile fragment shader: ${glGetShaderInfoLog(it)}")
            }
        }

        program.also {
            glAttachShader(it, vertexShader)
            glAttachShader(it, fragmentShader)
            glLinkProgram(it)
            glGetProgramiv(it, GL_LINK_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot link program: ${glGetProgramInfoLog(it)}")
            }
            glValidateProgram(it)
            glGetProgramiv(it, GL_VALIDATE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot validate program: ${glGetProgramInfoLog(it)}")
            }
            glUseProgram(it)
        }

        positionAttributeLocation = glGetAttribLocation(program, "position")
        colorAttributeLocation = glGetAttribLocation(program, "color")
        modelIndexAttributeLocation = glGetAttribLocation(program, "modelIndex")
        modelMatrixLocation = glGetUniformLocation(program, "M")
        viewMatrixLocation = glGetUniformLocation(program, "V")
        projectionMatrixLocation = glGetUniformLocation(program, "P")

        assertTrue(positionAttributeLocation >= 0)
        assertTrue(colorAttributeLocation >= 0)
        assertTrue(modelIndexAttributeLocation >= 0)
        assertTrue(modelMatrixLocation >= 0)
        assertTrue(viewMatrixLocation >= 0)
        assertTrue(projectionMatrixLocation >= 0)
    }

    /**
     * Upload the first [matrixCount] matrices from [buffer] into the model matrix uniform array.
     */
    fun uploadModelMatrixBuffer(buffer: MatrixBuffer, matrixCount: Int) {
        glUniformMatrix4fv(modelMatrixLocation, matrixCount, false, buffer.array, 0)
    }

    /**
     * Upload the first matrix in [buffer] to the view matrix uniform.
     */
    fun uploadViewMatrix(buffer: MatrixBuffer) {
        glUniformMatrix4fv(viewMatrixLocation, 1, false, buffer.array, 0)
    }

    /**
     * Upload the first matrix in [buffer] to the projection matrix uniform.
     */
    fun uploadProjectionMatrix(buffer: MatrixBuffer) {
        glUniformMatrix4fv(projectionMatrixLocation, 1, false, buffer.array, 0)
    }

}
