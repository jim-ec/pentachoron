package io.jim.tesserapp.rendering

import android.content.res.AssetManager
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import io.jim.tesserapp.graphics.DrawDataProvider
import io.jim.tesserapp.graphics.blue
import io.jim.tesserapp.graphics.green
import io.jim.tesserapp.graphics.red
import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.matrix.ViewMatrix
import io.jim.tesserapp.ui.model.MainViewModel
import io.jim.tesserapp.util.synchronized
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(
        val clearColor: Int,
        val viewModel: MainViewModel,
        val assets: AssetManager,
        val dpi: Double) : GLSurfaceView.Renderer {

    private val drawDataProvider = DrawDataProvider()

    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = Matrix(4).apply { perspective2D(near = 0.1, far = 100.0) }
    private val viewMatrix = ViewMatrix()

    private var aspectRatio: Double = 1.0

    companion object {

        /**
         * Converts inches to millimeters.
         */
        private const val MM_PER_INCH = 25.4

        /**
         * Specifies width of lines, in millimeters.
         */
        private const val LINE_WIDTH_MM = 0.15

    }

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(
                clearColor.red,
                clearColor.green,
                clearColor.blue,
                1f
        )
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        GLES30.glLineWidth((dpi / MM_PER_INCH * LINE_WIDTH_MM).toFloat())

        println("Open GLES version: ${GLES30.glGetString(GLES30.GL_VERSION)}")
        println("GLSL version: ${GLES30.glGetString(GLES30.GL_SHADING_LANGUAGE_VERSION)}")
        println("Renderer: ${GLES30.glGetString(GLES30.GL_RENDERER)}")
        println("Vendor: ${GLES30.glGetString(GLES30.GL_VENDOR)}")

        // Construct shader:
        shader = Shader(assets)

        // Construct vertex buffer:
        vertexBuffer = VertexBuffer(shader)
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        aspectRatio = width.toDouble() / height
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        viewModel.synchronized {

            // Ensure vertex data is up-to-date:
            drawDataProvider.updateVertices(viewModel.geometries)

        }

        // Since that can change the vertex buffer's size, reallocate TBO as well:
        shader.transformFeedback?.allocate(
                vectorCapacity = drawDataProvider.vertexMemory.writtenElementCounts
        )

        shader.bound {

            // Recompute and upload view and perspective matrices:
            viewModel.synchronized {
                shader.uploadViewMatrix(viewMatrix(
                        viewModel.cameraDistance.smoothed,
                        aspectRatio,
                        viewModel.horizontalCameraRotation.smoothed,
                        viewModel.verticalCameraRotation.smoothed
                ))
            }

            shader.uploadProjectionMatrix(projectionMatrix)


            // Vertex memory was rewritten and needs to be uploaded to GL:
            vertexBuffer.upload(memory = drawDataProvider.vertexMemory)

            // Draw the vertex buffer:
            vertexBuffer.draw(elementCounts = drawDataProvider.vertexMemory.writtenElementCounts)

        }

    }

}
