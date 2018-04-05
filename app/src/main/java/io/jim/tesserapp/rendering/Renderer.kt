package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.GeometryManager
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(context: Context) : GLSurfaceView.Renderer {

    /**
     * Root geometry of this renderer.
     */
    val rootGeometry
        get() = geometryManager.rootGeometry

    private val clearColor = Color(context, android.R.color.background_light)
    private val viewMatrix = MatrixBuffer(3)
    private val projectionMatrix = MatrixBuffer(1)
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer
    private var uploadVertexBuffer = true
    private var uploadModelMatrixBuffer = true
    private val geometryManager = GeometryManager(MAX_MODELS, MAX_VERTICES).apply {
        onVertexBufferUpdated += { uploadVertexBuffer = true }
        onModelMatrixBufferUpdated += { uploadModelMatrixBuffer = true }
    }

    companion object {
        private const val MAX_MODELS = 100
        private const val MAX_VERTICES = 1000
    }

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        shader = Shader(MAX_MODELS)
        vertexBuffer = VertexBuffer()

        projectionMatrix.MemorySpace().perspective2D(0, 0.1f, 100f)
        shader.uploadProjectionMatrix(projectionMatrix)
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        viewMatrix.MemorySpace().apply {
            lookAt(1, Vector(4.0, 0.0, 0.0, 1.0), Vector(0.0, 0.0, 0.0, 1.0), Vector(0.0, 1.0, 0.0, 1.0))
            scale(2, Vector(1.0, width.toDouble() / height, 1.0, 1.0))
            multiply(1, 2, 0)
        }

        shader.uploadViewMatrix(viewMatrix)
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (uploadVertexBuffer) {
            vertexBuffer.bind(shader, geometryManager.vertexBuffer)
            uploadVertexBuffer = false
        }

        if (uploadModelMatrixBuffer) {
            //println("Upload model matrix data")
            shader.uploadModelMatrixBuffer(geometryManager.modelMatrixBuffer.modelMatrixBuffer, geometryManager.modelMatrixBuffer.activeGeometries)
            uploadModelMatrixBuffer = false
        }

        // Draw actual geometry:
        glDrawArrays(GL_LINES, 0, geometryManager.vertexBuffer.activeEntries)
    }

}
