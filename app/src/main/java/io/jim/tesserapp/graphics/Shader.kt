package io.jim.tesserapp.graphics

import android.opengl.GLES20.*
import io.jim.tesserapp.math.Matrix
import junit.framework.Assert.assertTrue

class Shader {

    companion object {

        const val VERTEX_SHADER_SOURCE = """
            uniform mat4 P;
            uniform mat4 V;
            uniform mat4 M[100];

            attribute vec3 position;
            attribute vec3 color;
            attribute float modelIndex;

            varying vec3 vColor;

            void main() {
                gl_Position = P * V * M[int(modelIndex)] * vec4(position, 1.0);
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
    val modelIndexAttributeLocation: Int
    val projectionMatrixArray = FloatArray(16)
    val viewMatrixArray = FloatArray(16)

    init {
        assertTrue(vertexShader >= 0)
        assertTrue(fragmentShader >= 0)
        assertTrue(program >= 0)

        val status = IntArray(1)

        vertexShader.also {
            glShaderSource(it, VERTEX_SHADER_SOURCE)
            glCompileShader(it)
            glGetShaderiv(it, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot compile vertex shader: ${glGetShaderInfoLog(it)}")
            }
        }

        fragmentShader.also {
            glShaderSource(it, FRAGMENT_SHADER_SOURCE)
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

    fun uploadModelMatrixArray(array: FloatArray, matrixCount: Int) {
        glUniformMatrix4fv(modelMatrixLocation, matrixCount, false, array, 0)
    }

    fun uploadViewMatrix(matrix: Matrix) {
        matrix.storeToFloatArray(viewMatrixArray, 0)
        glUniformMatrix4fv(viewMatrixLocation, 1, false, viewMatrixArray, 0)
    }

    fun uploadProjectionMatrix(matrix: Matrix) {
        matrix.storeToFloatArray(projectionMatrixArray, 0)
        glUniformMatrix4fv(projectionMatrixLocation, 1, false, projectionMatrixArray, 0)
    }

}
