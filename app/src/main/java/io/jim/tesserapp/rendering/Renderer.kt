package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.GeometryManager
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.ViewMatrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(context: Context) : GLSurfaceView.Renderer {

    /**
     * Render data shared across this render thread an others.
     */
    val sharedRenderData = SharedRenderData(GeometryManager(MAX_MODELS))

    private val clearColor = Color(context, android.R.color.background_light)
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = MatrixBuffer(1)
    private val viewMatrix = ViewMatrix(sharedRenderData.camera)

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

        sharedRenderData.geometryManager.vertexBufferRewritten += { buffer ->
            vertexBuffer.bind(shader, buffer)
        }
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        sharedRenderData.camera.aspectRatio = width.toFloat() / height.toFloat()
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        sharedRenderData.synchronized { renderData ->

            // Recompute view matrix:
            viewMatrix.compute()
            shader.uploadViewMatrix(viewMatrix.buffer)

            // Upload model matrices:
            renderData.geometryManager.computeModelMatrices()
            shader.uploadModelMatrixBuffer(
                    renderData.geometryManager.modelMatrixBuffer.modelMatrixBuffer,
                    renderData.geometryManager.modelMatrixBuffer.activeGeometries)

            // Ensure vertex data is up-to-date:
            renderData.geometryManager.updateVertexBuffer()

            // Draw actual geometry:
            glDrawArrays(
                    GL_LINES, 0,
                    renderData.geometryManager.vertexBuffer.writtenElementCounts)
        }
    }

}
