package io.jim.tesserapp.graphics

import android.content.Context
import android.opengl.GLES10.GL_MULTISAMPLE
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(maxLines: Int, context: Context) : GLSurfaceView.Renderer {

    /**
     * The root for every spatial.
     */
    val rootSpatial = Spatial()

    private val maxLineVertices = maxLines * 2
    private lateinit var shader: Shader
    private lateinit var geometryBuffer: GeometryBuffer
    private var rebuildGeometryBuffers = true
    private val clearColor = Color(context, android.R.color.background_light)
    private val viewMatrix = Matrix()
    private val modelMatrixArray = FloatArray(16 * maxLineVertices)

    /**
     * Initialize data.
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(clearColor.red, clearColor.green, clearColor.blue, 1.0f)
        glDisable(GL_CULL_FACE)
        glEnable(GL_DEPTH_TEST)
        glEnable(GL_MULTISAMPLE)
        glLineWidth(4f)

        shader = Shader()
        geometryBuffer = GeometryBuffer(maxLineVertices)

        shader.uploadProjectionMatrix(Matrix().perspective(0.1, 100.0))

        // Rebuild geometry buffers upon spatial hierarchy change:
        Spatial.addChildrenChangedListener { rebuildGeometryBuffers = true }
    }

    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)
        viewMatrix.multiplicationFrom(
                Matrix().lookAt(Vector(4.0, 0.0, 0.0, 1.0), Vector(0.0, 0.0, 0.0, 1.0), Vector(0.0, 1.0, 0.0, 1.0)),
                Matrix().scale(Vector(1.0, width.toDouble() / height, 1.0, 1.0)))
        shader.uploadViewMatrix(viewMatrix)
    }

    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

        if (rebuildGeometryBuffers) {
            geometryBuffer.recordGeometries(rootSpatial)
            rebuildGeometryBuffers = false
        }

        geometryBuffer.bind(shader)

        // Fetch model matrices:
        var modelMatrixOffset = 0
        var modelIndex = 0
        geometryBuffer.forEachGeometry { modelMatrix ->
            modelMatrix.storeToFloatArray(modelMatrixArray, modelMatrixOffset)
            modelMatrixOffset += 16
            modelIndex++
        }
        shader.uploadModelMatrixArray(modelMatrixArray, modelMatrixOffset / 16)

        // Draw actual geometry:
        glDrawElements(GL_LINES, geometryBuffer.indexCount(), GL_UNSIGNED_INT, 0)
    }

}
