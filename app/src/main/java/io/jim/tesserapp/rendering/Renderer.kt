package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.GeometryManager
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(context: Context) : GLSurfaceView.Renderer {

    /**
     * Render data shared across this render thread an others.
     */
    val sharedRenderData = SharedRenderData(
            GeometryManager(MAX_MODELS),
            4f
    )

    private val clearColor = Color(context, android.R.color.background_light)
    private val viewMatrix = MatrixBuffer(3)
    private val viewMatrixMemory = viewMatrix.MemorySpace()
    private val projectionMatrix = MatrixBuffer(1)
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer
    private var viewportAspectRation = 1f

    companion object {
        private const val MAX_MODELS = 100
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
        viewportAspectRation = width.toFloat() / height.toFloat()
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        synchronized(sharedRenderData) {
            // Recompute view matrix:
            viewMatrixMemory.apply {
                lookAt(1, Vector(sharedRenderData.cameraDistance, 0f, 0f, 1f), Vector(0f, 0f, 0f, 1f), Vector(0f, 1f, 0f, 1f))
                scale(2, Vector(1f, viewportAspectRation, 1f, 1f))
                multiply(1, 2, 0)
                shader.uploadViewMatrix(viewMatrix)
            }

            // Upload model matrices:
            sharedRenderData.geometryManager.computeModelMatrices()
            shader.uploadModelMatrixBuffer(
                    sharedRenderData.geometryManager.modelMatrixBuffer.modelMatrixBuffer,
                    sharedRenderData.geometryManager.modelMatrixBuffer.activeGeometries)

            // Recompute geometry vertices:
            if (sharedRenderData.geometryManager.verticesUpdated) {
                vertexBuffer.bind(shader, sharedRenderData.geometryManager.vertexBuffer)
                sharedRenderData.geometryManager.verticesUpdated = false
            }

            // Draw actual geometry:
            glDrawArrays(
                    GL_LINES, 0,
                    (sharedRenderData.geometryManager.vertexBuffer.lastActiveElementIndex + 1))
        }
    }

}
