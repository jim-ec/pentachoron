package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxTriangles: Int, maxLines: Int) : GLSurfaceView.Renderer {

    private val maxTriangleVertices = maxTriangles * 3
    private val maxLineVertices = maxLines * 2
    private lateinit var triangleBuffer: VertexBuffer
    private lateinit var lineBuffer: VertexBuffer
    private lateinit var shader: Shader

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1f, 1f, 1f, 1f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        shader = Shader()
        triangleBuffer = VertexBuffer(maxTriangleVertices)
        lineBuffer = VertexBuffer(maxLineVertices)
    }

    fun appendLine(a: Vector, b: Vector, color: Color) {
        lineBuffer.apply {
            appendVertex(a, color)
            appendVertex(b, color)
        }
    }

    fun appendTriangle(a: Vector, b: Vector, c: Vector, color: Color) {
        triangleBuffer.apply {
            appendVertex(a, color)
            appendVertex(b, color)
            appendVertex(c, color)
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        triangleBuffer.draw(shader, GL_TRIANGLES)
        lineBuffer.draw(shader, GL_LINES)
    }

}
