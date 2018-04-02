package io.jim.tesserapp.graphics

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Renderer(maxLines: Int, context: Context) : GLSurfaceView.Renderer {

    private val maxLineVertices = maxLines * 2
    private lateinit var shader: Shader
    private lateinit var geometryBuffer: GeometryBuffer
    private var uploadGeometryBuffers = false
    private val clearColor = Color(context, android.R.color.background_light)
    private val viewMatrix = Matrix(3)
    val rootSpatial = Spatial(3, fun() { uploadGeometryBuffers = true })

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        shader = Shader()
        geometryBuffer = GeometryBuffer(maxLineVertices)

        shader.uploadProjectionMatrix(Matrix(3).perspective(0.1, 100.0))
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        viewMatrix.multiplicationFrom(Matrix(3).lookAt(Vector(4.0, 4.0, 0.0), Vector(3), Vector(0.0, 1.0, 0.0)),
                Matrix(3).scale(Vector(1.0, width.toDouble() / height, 1.0)))
        shader.uploadViewMatrix(viewMatrix)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (uploadGeometryBuffers) {
            geometryBuffer.recordGeometries(rootSpatial)
            uploadGeometryBuffers = false
        }

        geometryBuffer.bind(shader)

        geometryBuffer.forEachGeometry { geometry, offset, indexCount ->
            shader.uploadModelMatrix(geometry.modelMatrix())
            glDrawElements(GL_LINES, indexCount, GL_UNSIGNED_INT, offset * IndexBuffer.INDEX_BYTE_LENGTH)
        }
    }

}
