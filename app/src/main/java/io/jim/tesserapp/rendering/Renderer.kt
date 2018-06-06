package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.*
import io.jim.tesserapp.math.matrix.Projection3dMatrix
import io.jim.tesserapp.math.matrix.ViewMatrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * Actually renders to OpenGL.
 */
class Renderer(private val context: Context, private val dpi: Double) : GLSurfaceView.Renderer {

    /**
     * Render data shared across this render thread an others.
     */
    val sharedRenderData = SharedRenderData(
            drawDataProvider = DrawDataProvider(),
            featuredGeometry = Geometry("Featured Geometry", colorInt(context, R.color.lightAccent))
    )
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = Projection3dMatrix(near = 0.1, far = 100.0)
    private val viewMatrix = ViewMatrix(sharedRenderData.camera)

    private val clearColor =
            colorInt(context, R.style.LightTheme, android.R.attr.windowBackground).apply {
                //luminance(0.8)
            }

    companion object {

        /**
         * Converts inches to millimeters.
         */
        const val MM_PER_INCH = 25.4

        /**
         * Specifies width of lines, in millimeters.
         */
        const val LINE_WIDTH_MM = 0.15

    }

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(
                redFloat(clearColor),
                greenFloat(clearColor),
                blueFloat(clearColor),
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
        shader = Shader(context.assets)

        // Construct vertex buffer:
        vertexBuffer = VertexBuffer(
                shader,
                sharedRenderData.drawDataProvider.vertexMemory
        )
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        sharedRenderData.camera.aspectRatio = width.toDouble() / height
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        sharedRenderData.synchronized { (drawDataProvider) ->

            // Ensure vertex data is up-to-date:
            drawDataProvider.updateVertices()

            // Vertex memory was rewritten and needs to be uploaded to GL:
            vertexBuffer.upload()

            // Since that can change the vertex buffer's size, reallocate TBO as well:
            shader.transformFeedback?.allocate(vertexBuffer.memory.writtenElementCounts)

            shader.bound {

                // Recompute and upload view and perspective matrices:
                shader.uploadViewMatrix(viewMatrix.apply { compute() })
                shader.uploadProjectionMatrix(projectionMatrix)

                // Draw the vertex buffer:
                vertexBuffer.draw()

            }
        }
    }

}
