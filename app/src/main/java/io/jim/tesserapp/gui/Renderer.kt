package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.math.Vector
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxTriangles: Int) : GLSurfaceView.Renderer {

    companion object {
        private const val COMPONENTS_PER_POSITION = 3
        private const val COMPONENTS_PER_COLOR = 3
        const val COMPONENTS_PER_VERTEX = COMPONENTS_PER_POSITION + COMPONENTS_PER_COLOR
        const val FLOAT_BYTE_LENGTH = 4
        const val VERTEX_BYTE_LENGTH = COMPONENTS_PER_VERTEX * FLOAT_BYTE_LENGTH
    }

    private val maxTriangleVertices = maxTriangles * 3

    private val byteBuffer =
            ByteBuffer.allocateDirect(maxTriangleVertices * VERTEX_BYTE_LENGTH)
                    .apply { order(ByteOrder.nativeOrder()) }

    private val floatBuffer = byteBuffer.asFloatBuffer().apply {
        clear()
        while (position() < capacity()) {
            put(0f)
        }
        rewind()
    }

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
    private var buffer = -1

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

        buffer = let {
            glGenBuffers(1, status, 0)
            status[0]
        }
    }

    fun clearVertices() {
        floatBuffer.clear()
    }

    /*private fun appendLine(line: Pair<Vector, Vector>, color: Color) {
        appendVertex(line.first, color)
        appendVertex(line.second, color)
    }*/

    fun appendTriangle(a: Vector, b: Vector, c: Vector, color: Color) {
        appendVertex(a, color)
        appendVertex(b, color)
        appendVertex(c, color)
    }

    private fun appendVertex(position: Vector, color: Color) {
        require(3 == position.size) { "Position vectors must be 3D" }
        require(floatBuffer.position() + COMPONENTS_PER_VERTEX <= floatBuffer.capacity())
        {
            "Insufficient memory to store vertex: pos=%d(%d verts)  cap=%d(%d verts)  needed=%d"
                    .format(floatBuffer.position(), floatBuffer.position() / COMPONENTS_PER_VERTEX,
                            floatBuffer.capacity(), floatBuffer.capacity() / COMPONENTS_PER_VERTEX,
                            COMPONENTS_PER_VERTEX)
        }

        floatBuffer.apply {
            put(position.x.toFloat())
            put(position.y.toFloat())
            put(position.z.toFloat())
            put(color.red)
            put(color.green)
            put(color.blue)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        println("Vertex buffer: " +
                "pos=${floatBuffer.position()} " +
                "cap=${floatBuffer.capacity()} ")

        floatBuffer.rewind()
        glBindBuffer(GL_ARRAY_BUFFER, buffer)
        glBufferData(GL_ARRAY_BUFFER, maxTriangleVertices * VERTEX_BYTE_LENGTH, floatBuffer, GL_DYNAMIC_DRAW)

        glGetAttribLocation(program, "position").apply {
            glEnableVertexAttribArray(this)
            glVertexAttribPointer(this, COMPONENTS_PER_POSITION, GL_FLOAT, false, VERTEX_BYTE_LENGTH, 0)
        }

        glGetAttribLocation(program, "color").apply {
            glEnableVertexAttribArray(this)
            glVertexAttribPointer(this, COMPONENTS_PER_COLOR, GL_FLOAT, false, VERTEX_BYTE_LENGTH, COMPONENTS_PER_POSITION * FLOAT_BYTE_LENGTH)
        }

        glDrawArrays(GL_TRIANGLES, 0, maxTriangleVertices)
    }

}
