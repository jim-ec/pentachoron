package io.jim.tesserapp.rendering

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class RendererOld(context: Context) : GLSurfaceView.Renderer {

    /**
     * The root for every geometry.
     */
    val rootGeometry = Geometry("Root")

    private lateinit var shader: Shader
    private lateinit var geometryBufferOld: GeometryBufferOld
    private var rebuildGeometryBuffers = true
    private val clearColor = Color(context, android.R.color.background_light)
    private val viewMatrix = MatrixBuffer(3)
    private val projectionMatrix = MatrixBuffer(1)

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
        geometryBufferOld = GeometryBufferOld(MAX_MODELS, MAX_VERTICES)

        projectionMatrix.MemorySpace().perspective2D(0, 0.1f, 100f)
        shader.uploadProjectionMatrix(projectionMatrix)

        // Rebuild geometry buffers upon hierarchy or geometry change:
        Geometry.onHierarchyChangedListeners += { rebuildGeometryBuffers = true }
        Geometry.onGeometryChangedListeners += { rebuildGeometryBuffers = true }
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

        // If geometry structure changes, we need to rebuild all buffers.
        // This is a costly operation, but usually not often performed.
        if (rebuildGeometryBuffers) {
            synchronized(Geometry) {
                println("Rebuild geometry buffers")
                geometryBufferOld.recordGeometries(rootGeometry)
                rebuildGeometryBuffers = false
            }
        }

        // Recompute global model matrices.
        // Since render is only requested when data is changed, and geometry which is not
        // transformed often is not expected in large chunks, we can simply re-compute all
        // global model matrices:
        rootGeometry.computeModelMatricesRecursively()

        geometryBufferOld.bind(shader)

        // Re-upload global model matrices:
        shader.uploadModelMatrixBuffer(geometryBufferOld.globalMatrixBuffer, geometryBufferOld.activeGeometries)

        // Draw actual geometry:
        glDrawArrays(GL_LINES, 0, geometryBufferOld.vertexCount)
    }

}
