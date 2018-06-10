package io.jim.tesserapp.rendering

import android.opengl.GLES30
import android.opengl.GLSurfaceView
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.*
import io.jim.tesserapp.math.matrix.Matrix
import io.jim.tesserapp.math.matrix.ViewMatrix
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(private val context: MainActivity, private val dpi: Double) : GLSurfaceView.Renderer {

    val featuredGeometry = Geometry(
            onTransformUpdate = {
                // Transform geometry in each frame relatively,
                // by using the difference value returned from the smooth-delegates:

                with(context.viewModel) {

                    rotateX(rotationX.smoothed * Math.PI)
                    rotateY(rotationY.smoothed * Math.PI)
                    rotateZ(rotationZ.smoothed * Math.PI)

                    translateX(translationX.smoothed)
                    translateY(translationY.smoothed)
                    translateZ(translationZ.smoothed)

                }

            }
    )

    private val drawDataProvider = DrawDataProvider()

    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = Matrix(4).apply { perspective2D(near = 0.1, far = 100.0) }
    private val viewMatrix = ViewMatrix()

    private var aspectRatio: Double = 1.0
    private val clearColor = themedColorInt(context, android.R.attr.windowBackground)

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

    init {
        featuredGeometry.apply {
            name = "Featured Geometry"
            baseColor = themedColorInt(context, R.attr.colorAccent)

            drawDataProvider += this
        }
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
        shader = Shader(context.assets)

        // Construct vertex buffer:
        vertexBuffer = VertexBuffer(
                shader,
                drawDataProvider.vertexMemory
        )
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

        // Ensure vertex data is up-to-date:
        drawDataProvider.updateVertices()

        // Vertex memory was rewritten and needs to be uploaded to GL:
        vertexBuffer.upload()

        // Since that can change the vertex buffer's size, reallocate TBO as well:
        shader.transformFeedback?.allocate(vertexBuffer.memory.writtenElementCounts)

        shader.bound {

            // Recompute and upload view and perspective matrices:
            shader.uploadViewMatrix(viewMatrix(
                    context.viewModel.cameraDistance.smoothed,
                    aspectRatio,
                    context.viewModel.horizontalCameraRotation.smoothed,
                    context.viewModel.verticalCameraRotation.smoothed
            ))
            shader.uploadProjectionMatrix(projectionMatrix)

            // Draw the vertex buffer:
            vertexBuffer.draw()

        }

    }

    /**
     * Register [geometry] to be drawn by this renderer.
     */
    fun addGeometry(geometry: Geometry) {
        drawDataProvider += geometry
    }

    /**
     * [geometry] is not drawn anymore by this renderer.
     */
    fun removeGeometry(geometry: Geometry) {
        drawDataProvider -= geometry
    }

}
