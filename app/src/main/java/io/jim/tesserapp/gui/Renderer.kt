package io.jim.tesserapp.gui

import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.Direction
import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Point
import junit.framework.Assert.assertEquals
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxLines: Int) : GLSurfaceView.Renderer {

    private val geometries = ArrayList<Geometry>()
    private val maxLineVertices = maxLines * 2
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer
    private lateinit var indexBuffer: IndexBuffer
    private var uploadGeometryBuffers = false
    private val viewMatrix = Matrix(3)
    private val lookAtMatrix = Matrix(3).apply { lookAt(Point(2.0, 2.5, 3.0), Point(0.0, 0.0, 0.0), Direction(0.0, 1.0, 0.0)) }
    private val zxRotationMatrix = Matrix(3)
    private val xyRotationMatrix = Matrix(3)
    private val projectionMatrix = Matrix(3).perspective(0.1, 100.0)
    private val translationMatrix = Matrix(3)//.translation(Direction(0.0, 0.0, -4.0))
    private var uploadMatrices = false

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(1f, 1f, 1f, 1f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        shader = Shader()
        vertexBuffer = VertexBuffer(maxLineVertices)
        indexBuffer = IndexBuffer(maxLineVertices)
    }

    fun addGeometry(geometry: Geometry) {
        geometries.add(geometry)
        uploadGeometryBuffers = true
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        viewMatrix.scale(Point(1.0, width.toDouble() / height, 1.0))
        uploadMatrices = true
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (uploadMatrices) {
            shader.uploadMatrix(zxRotationMatrix * xyRotationMatrix * translationMatrix * lookAtMatrix * viewMatrix * projectionMatrix)
            uploadMatrices = false
        }

        if (uploadGeometryBuffers) {
            indexBuffer.baseIndex = 0
            for (geometry in geometries) {
                for (point in geometry.points) {
                    assertEquals("All vertices must be 3D", 3, point.dimension)
                    vertexBuffer.appendVertex(point, geometry.color)
                }
                for (line in geometry.lines) {
                    indexBuffer.appendIndex(line.first)
                    indexBuffer.appendIndex(line.second)
                }
                indexBuffer.baseIndex += geometry.points.size
            }
            uploadGeometryBuffers = false
        }

        vertexBuffer.bind(shader)
        indexBuffer.bind()

        glDrawElements(GL_LINES, indexBuffer.size, GL_UNSIGNED_INT, 0)
    }

    fun rotation(horizontal: Double, vertical: Double) {
        zxRotationMatrix.rotation(2, 0, horizontal)
        xyRotationMatrix.rotation(1, 0, vertical)
        uploadMatrices = true
    }

}
