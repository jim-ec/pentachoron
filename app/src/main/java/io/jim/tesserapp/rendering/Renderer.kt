package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES30
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
class Renderer(context: Context, private val dpi: Float) : GLSurfaceView.Renderer {

    /**
     * Render data shared across this render thread an others.
     */
    val sharedRenderData = SharedRenderData(GeometryManager())

    private val clearColor = Color(context, android.R.color.background_light)
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: VertexBuffer

    private val projectionMatrix = Projection3dMatrix(near = 0.1f, far = 100f)
    private val viewMatrix = ViewMatrix(sharedRenderData.camera)

    companion object {

        const val MM_PER_INCH = 25.4f

        const val LINE_WIDTH_MM = 0.15f

    }

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        GLES30.glDisable(GLES30.GL_CULL_FACE)
        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        GLES30.glLineWidth(dpi / MM_PER_INCH * LINE_WIDTH_MM)

        println("Open GLES version: ${GLES30.glGetString(GLES30.GL_VERSION)}")
        println("GLSL version: ${GLES30.glGetString(GLES30.GL_SHADING_LANGUAGE_VERSION)}")
        println("Renderer: ${GLES30.glGetString(GLES30.GL_RENDERER)}")
        println("Vendor: ${GLES30.glGetString(GLES30.GL_VENDOR)}")

        shader = Shader()
        vertexBuffer = VertexBuffer(
                shader,
                sharedRenderData.geometryManager.backingVertexBuffer
        )

        shader.bound {
            shader.uploadProjectionMatrix(projectionMatrix)
        }
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)
        sharedRenderData.camera.aspectRatio = width.toFloat() / height.toFloat()
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        sharedRenderData.synchronized { (geometryManager) ->

            // Ensure vertex data is up-to-date:
            if (geometryManager.updateVertexBuffer()) {
                vertexBuffer.write()
                shader.transformFeedback?.allocate(vertexBuffer.backingBuffer.writtenElementCounts)
            }

            shader.bound {

                // Recompute view matrix:
                shader.uploadViewMatrix(viewMatrix.apply { compute() })

                // Upload model matrices:
                geometryManager.computeModelMatrices()
                shader.uploadModelMatrixBuffer(
                        geometryManager.modelMatrixBuffer.buffer,
                        geometryManager.modelMatrixBuffer.activeGeometries)

                // Bind VAO:
                vertexBuffer.draw()

            }
        }
    }

}
