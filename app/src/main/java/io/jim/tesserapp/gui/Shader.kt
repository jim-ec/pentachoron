package io.jim.tesserapp.gui

import android.opengl.GLES20.*

class Shader {

    companion object {

        const val VERTEX_SHADER_SOURCE = """
            attribute vec3 position;
            attribute vec3 color;

            varying vec3 vColor;

            void main() {
                gl_Position = vec4(position, 1.0);
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
    val program = glCreateProgram()

    init {
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
    }

}
