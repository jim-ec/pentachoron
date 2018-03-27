package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import io.jim.tesserapp.math.Matrix
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue

class Shader {

    companion object {

        const val VERTEX_SHADER_SOURCE = """
            uniform mat4 M;

            attribute vec3 position;
            attribute vec3 color;

            varying vec3 vColor;

            void main() {
                gl_Position = M * vec4(position, 1.0);
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
    private val matrixUniform: Int
    val positionAttribute: Int
    val colorAttribute: Int

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

        positionAttribute = glGetAttribLocation(program, "position")
        colorAttribute = glGetAttribLocation(program, "color")
        matrixUniform = glGetUniformLocation(program, "M")

        assertTrue(positionAttribute >= 0)
        assertTrue(colorAttribute >= 0)
        assertTrue(matrixUniform >= 0)
    }

    fun updateMatrix(matrix: Matrix) {
        assertEquals("Shader matrices must be 4x4 homogeneous", matrix.dimension, 3)
        val floats = FloatArray(16) { i -> matrix[i / 4][i % 4].toFloat() }
        glUniformMatrix4fv(matrixUniform, 1, false, floats, 0)
    }

}
