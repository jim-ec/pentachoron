package io.jim.tesserapp.graphics

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import io.jim.tesserapp.cpp.generateVertexBuffer
import io.jim.tesserapp.cpp.graphics.GlVertexBuffer
import io.jim.tesserapp.cpp.graphics.Shader
import io.jim.tesserapp.cpp.matrix.perspective
import io.jim.tesserapp.cpp.matrix.view
import io.jim.tesserapp.cpp.resolveToVertices
import io.jim.tesserapp.cpp.transformed
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
    
    private lateinit var shader: Shader
    private lateinit var vertexBuffer: GlVertexBuffer
    
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
        GLES20.glClearColor(
                clearColor.red,
                clearColor.green,
                clearColor.blue,
                1f
        )
        GLES20.glDisable(GLES20.GL_CULL_FACE)
        GLES20.glEnable(GLES20.GL_DEPTH_TEST)
    
        GLES20.glLineWidth((dpi / MM_PER_INCH * LINE_WIDTH_MM).toFloat())
    
        println("Open GLES version: ${GLES20.glGetString(GLES20.GL_VERSION)}")
        println("GLSL version: ${GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)}")
        println("Renderer: ${GLES20.glGetString(GLES20.GL_RENDERER)}")
        println("Vendor: ${GLES20.glGetString(GLES20.GL_VENDOR)}")
        
        // Construct shader:
        shader = Shader(assets)
        
        // Construct vertex buffer:
        vertexBuffer = generateVertexBuffer(shader)
    }
    
    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        aspectRatio = width.toDouble() / height
    }
    
    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
    
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    
        viewModel.synchronized {
        
            shader.program.bound {
                
                shader.uploadViewMatrix(view(
                        cameraDistance.smoothed,
                        aspectRatio,
                        horizontalCameraRotation.smoothed,
                        verticalCameraRotation.smoothed
                ))
            
                shader.uploadProjectionMatrix(perspective(near = 0.1, far = 100.0))
            
                // Process geometries and draw the generated vertices:
                geometries.flatMap {
                    it.transformed(fourthDimensionVisualizer)
                }.flatMap {
                    it.resolveToVertices(colorResolver)
                }.also {
                    vertexBuffer.draw(it)
                }
                
            }
            
        }
        
    }
    
}
