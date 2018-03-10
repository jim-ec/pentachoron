package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxTriangles: Int) : GLSurfaceView.Renderer {

    private val maxTriangleVertices = maxTriangles * 3

    private lateinit var triangleBuffer: VertexBuffer

    private val vertexShaderSource = """
        attribute vec3 position;
        attribute vec3 color;

        varying vec3 vColor;

        void main() {
            gl_Position = vec4(position, 1.0);
            vColor = color;
        }
    """.trimIndent()

    private val fragmentShaderSource = """
        varying mediump vec3 vColor;

        void main() {
            gl_FragColor = vec4(vColor, 1.0);
        }
    """.trimIndent()

    private var vertexShader = -1
    private var fragmentShader = -1
    private var program = -1

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1f, 1f, 1f, 1f)

        // Disabled due to transparency:
        glDisable(GL_CULL_FACE)
        glDisable(GL_DEPTH_TEST)

        glLineWidth(4f)

        val status = IntArray(1)

        vertexShader = glCreateShader(GL_VERTEX_SHADER)
        vertexShader.apply {
            glShaderSource(this, vertexShaderSource)
            glCompileShader(this)
            glGetShaderiv(this, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot compile vertex shader: ${glGetShaderInfoLog(this)}")
            }
        }

        fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        fragmentShader.apply {
            glShaderSource(this, fragmentShaderSource)
            glCompileShader(this)
            glGetShaderiv(this, GL_COMPILE_STATUS, status, 0)
            if (GL_TRUE != status[0]) {
                throw Exception("Cannot compile fragment shader: ${glGetShaderInfoLog(this)}")
            }
        }

        program = glCreateProgram()
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

        triangleBuffer = VertexBuffer(maxTriangleVertices)
    }

    /*private fun appendLine(line: Pair<Vector, Vector>, color: Color) {
        appendVertex(line.first, color)
        appendVertex(line.second, color)
    }*/

    fun appendTriangle(a: Vector, b: Vector, c: Vector, color: Color) {
        triangleBuffer.appendVertex(a, color)
        triangleBuffer.appendVertex(b, color)
        triangleBuffer.appendVertex(c, color)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        triangleBuffer.update(program)

        glDrawArrays(GL_TRIANGLES, 0, maxTriangleVertices)
    }

}
