package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.GeometryManager
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.math.transform.Projection3dMatrix
import io.jim.tesserapp.math.transform.ViewMatrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(context: Context) : GLSurfaceView.Renderer {

    /**
     * Render data shared across this render thread an others.
     */
    val sharedRenderData = SharedRenderData(GeometryManager())

    private val clearColor = Color(context, android.R.color.background_light)
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = Projection3dMatrix(near = 0.1f, far = 100f)
    private val viewMatrix = ViewMatrix(sharedRenderData.camera)

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glLineWidth(4f)

        println("Open GLES version: ${glGetString(GL_VERSION)}")
        println("GLSL version: ${glGetString(GL_SHADING_LANGUAGE_VERSION)}")

        shader = Shader()
        vertexBuffer = VertexBuffer()

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

        sharedRenderData.synchronized { (geometryManager) ->

            // Recompute view matrix:
            viewMatrix.compute()
            shader.uploadViewMatrix(viewMatrix)

            // Upload model matrices:
            geometryManager.computeModelMatrices()
            shader.uploadModelMatrixBuffer(
                    geometryManager.modelMatrixBuffer.buffer)

            // Ensure vertex data is up-to-date:
            geometryManager.updateVertexBuffer()

            // Draw actual geometry:
            glDrawArrays(
                    GL_LINES, 0,
                    geometryManager.vertexBuffer.writtenElementCounts)
        }
    }

}
