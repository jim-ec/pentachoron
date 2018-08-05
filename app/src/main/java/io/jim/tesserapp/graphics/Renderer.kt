/*
 *  Created by Jim Eckerlein on 7/23/18 9:34 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/23/18 9:34 AM
 */

package io.jim.tesserapp.graphics

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.graphics.gl.Vbo
import io.jim.tesserapp.ui.main.MainViewModel
import java.nio.DoubleBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Actually renders to OpenGL.
 */
class Renderer(
        val clearColor: Color,
        val viewModel: MainViewModel,
        val assets: AssetManager,
        val dpi: Double) : GLSurfaceView.Renderer {
    
    private lateinit var linesShader: LinesShader
    private lateinit var vertexVbo: Vbo
    
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
        
        init {
            System.loadLibrary("native-lib")
        }
        
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
        linesShader = LinesShader(assets)
        
        // Construct vertex buffer:
        vertexVbo = Vbo(GLES20.GL_ARRAY_BUFFER)
    }
    
    /**
     * Construct view matrix.
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
        aspectRatio = width.toDouble() / height
    }
    
    /**
     * Uploads the view matrix.
     * Requires the GL program to be bound.
     */
    external fun uploadViewMatrix(
            uniformLocation: Int,
            distance: Double,
            aspectRatio: Double,
            horizontalRotation: Double,
            verticalRotation: Double,
            fovX: Double
    )
    
    /**
     * Uploads the projection matrix.
     * Requires the GL program to be bound.
     */
    external fun uploadProjectionMatrix(
            uniformLocation: Int
    )
    
    /**
     * Draws a single geometry.
     * Requires the GL program to be bound.
     * Requires the VBO bound to GL_ARRAY_BUFFER.
     * Vertex attribute pointers must be instructed before drawing anything.
     */
    external fun drawGeometry(
            positionAttributeLocation: Int,
            colorAttributeLocation: Int,
            positions: DoubleBuffer,
            transform: DoubleArray,
            color: Int,
            isFourDimensional: Boolean
    )
    
    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        synchronized(viewModel) {
            
            linesShader.program.bound {
                
                uploadViewMatrix(
                        linesShader.viewMatrixLocation,
                        viewModel.cameraDistance.value,
                        aspectRatio,
                        viewModel.horizontalCameraRotation.value,
                        viewModel.verticalCameraRotation.value,
                        viewModel.cameraFovX.value
                )
                
                uploadProjectionMatrix(linesShader.projectionMatrixLocation)
                
                viewModel.geometries.forEach { geometry ->
                    
                    val transform = geometry.onTransformUpdate()
                    
                    vertexVbo.bound {
                        
                        //instructVertexAttributePointers(linesShader)
                        
                        drawGeometry(
                                linesShader.positionAttributeLocation,
                                linesShader.colorAttributeLocation,
                                geometry.positions,
                                transform.data,
                                geometry.color.code,
                                geometry.isFourDimensional
                        )
                        
                    }
                    
                }
                
                
            }
            
        }
        
    }
    
}
