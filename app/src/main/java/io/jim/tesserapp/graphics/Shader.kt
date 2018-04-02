package io.jim.tesserapp.graphics

import android.opengl.GLES20.*
import io.jim.tesserapp.math.Matrix
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue

class Shader {

    companion object {

        const val VERTEX_SHADER_SOURCE = """
            uniform mat4 P;
            uniform mat4 V;
            uniform mat4 M;

            attribute vec3 position;
            attribute vec3 color;

            varying vec3 vColor;

            void main() {
                gl_Position = P * V * M * vec4(position, 1.0);
                vColor = color;
            }
        """

        const val FRAGMENT_SHADER_SOURCE = """
            varying mediump vec3 vColor;

            void main() {
                gl_FragColor = vec4(vColor, 1.0);
            }
        """

    }

    private val vertexShader = glCreateShader(GL_VERTEX_SHADER)
    private val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
    private val program = glCreateProgram()
    private val modelMatrixLocation: Int
    private val viewMatrixLocation: Int
    private val projectionMatrixLocation: Int
    val positionAttributeLocation: Int
    val colorAttributeLocation: Int

    init {
        assertTrue(vertexShader >= 0)
        assertTrue(fragmentShader >= 0)
        assertTrue(program >= 0)

        val status = IntArray(1)

        vertexShader.apply {
            glShaderSource(this, VERTEX_SHADER_SOURCE)
            glCompileShader(this)
            glGetShaderiv(this, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot compile vertex shader: ${glGetShaderInfoLog(this)}")
            }
        }

        fragmentShader.apply {
            glShaderSource(this, FRAGMENT_SHADER_SOURCE)
            glCompileShader(this)
            glGetShaderiv(this, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot compile fragment shader: ${glGetShaderInfoLog(this)}")
            }
        }

        program.apply {
            glAttachShader(this, vertexShader)
            glAttachShader(this, fragmentShader)
            glLinkProgram(this)
            glGetProgramiv(this, GL_LINK_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot link program: ${glGetProgramInfoLog(this)}")
            }
            glValidateProgram(this)
            glGetProgramiv(this, GL_VALIDATE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot validate program: ${glGetProgramInfoLog(this)}")
            }
            glUseProgram(this)
        }

        positionAttributeLocation = glGetAttribLocation(program, "position")
        colorAttributeLocation = glGetAttribLocation(program, "color")
        modelMatrixLocation = glGetUniformLocation(program, "M")
        viewMatrixLocation = glGetUniformLocation(program, "V")
        projectionMatrixLocation = glGetUniformLocation(program, "P")

        assertTrue(positionAttributeLocation >= 0)
        assertTrue(colorAttributeLocation >= 0)
        assertTrue(modelMatrixLocation >= 0)
        assertTrue(viewMatrixLocation >= 0)
        assertTrue(projectionMatrixLocation >= 0)
    }

    fun uploadModelMatrix(matrix: Matrix) {
        assertEquals("Shader matrix must be 3D", matrix.dimension, 3)
        glUniformMatrix4fv(modelMatrixLocation, 1, false, matrix.toFloatArray(), 0)
    }

    fun uploadViewMatrix(matrix: Matrix) {
        assertEquals("Shader matrix must be 3D", matrix.dimension, 3)
        glUniformMatrix4fv(viewMatrixLocation, 1, false, matrix.toFloatArray(), 0)
    }

    fun uploadProjectionMatrix(matrix: Matrix) {
        assertEquals("Shader matrix must be 3D", matrix.dimension, 3)
        glUniformMatrix4fv(projectionMatrixLocation, 1, false, matrix.toFloatArray(), 0)
    }

}
