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
    val sharedRenderData = SharedRenderData()

    init {
        sharedRenderData.featuredGeometry.apply {
            name = "Featured Geometry"
            baseColor = themedColorInt(context, R.attr.colorAccent)
        }
    }

    /**
     * Geometry manager.
     */
    private val drawDataProvider = DrawDataProvider().apply {
        this += sharedRenderData.featuredGeometry
    }

    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = Projection3dMatrix(near = 0.1, far = 100.0)
    private val viewMatrix = ViewMatrix(sharedRenderData.camera)

    private val clearColor = themedColorInt(context, android.R.attr.windowBackground)

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
        synchronized {
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
            shader = Shader(context.assets)

            // Construct vertex buffer:
            vertexBuffer = VertexBuffer(
                    shader,
                    drawDataProvider.vertexMemory
            )
        }
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        synchronized {
            GLES30.glViewport(0, 0, width, height)
            sharedRenderData.camera.aspectRatio = width.toDouble() / height
        }
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        synchronized {

            GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

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

    /**
     * Register [geometry] to be drawn by this renderer.
     * This function is internally synchronized.
     */
    fun addGeometry(geometry: Geometry) {
        synchronized {
            drawDataProvider += geometry
        }
    }

    /**
     * [geometry] is not drawn anymore by this renderer.
     * This function is internally synchronized.
     */
    fun removeGeometry(geometry: Geometry) {
        synchronized {
            drawDataProvider -= geometry
        }
    }

    /**
     * Synchronizes inter-thread access to this renderer.
     * This is always necessary when accessing geometry, which has been registered into
     * this renderer by [addGeometry].
     *
     * @param f
     * Receives this renderer draw-data object, which must be only referenced
     * during the execution of [f].
     */
    inline fun synchronized(f: (renderData: SharedRenderData) -> Unit) {
        synchronized(this) {
            f(sharedRenderData)
        }
    }

}
