package io.jim.tesserapp.ui.view

import android.content.res.AssetManager
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import io.jim.tesserapp.cpp.*
import io.jim.tesserapp.cpp.graphics.Color
import io.jim.tesserapp.cpp.graphics.GlVertexBuffer
import io.jim.tesserapp.cpp.graphics.Shader
import io.jim.tesserapp.cpp.matrix.*
import io.jim.tesserapp.cpp.vector.VectorN
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.ui.model.MainViewModel
import io.jim.tesserapp.util.synchronized
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
        
        init {
            System.loadLibrary("native-lib")
        }
        
    }
    
    external fun init()
    
    external fun deinit()
    
    init {
        init()
    }
    
    @Suppress("unused")
    fun finalize() {
        deinit()
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
    
    external fun drawGeometry(
            geometry: Geometry,
            transform: Transform,
            color: Int
    )
    
    /**
     * Draw a single frame.
     */
    override fun onDrawFrame(gl: GL10?) {
        
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        
        viewModel.synchronized {
            
            val camera = Camera(
                    cameraDistance.smoothed,
                    aspectRatio,
                    horizontalCameraRotation.smoothed,
                    verticalCameraRotation.smoothed
            )
            
            shader.program.bound {
                
                shader.uploadViewMatrix(view(camera))
                shader.uploadProjectionMatrix(perspective(near = 0.1, far = 100.0))
                
                geometries.forEach { geometry ->
                    
                    val transform = geometry.onTransformUpdate()
                    val color = symbolicColorMapping[geometry.color]
                    val positions = geometry.positions
                    val isFourDimensional = geometry.isFourDimensional
                    val fourthDimensionVisualizationMode = fourthDimensionVisualizationMode.id
                    
                    val modelMatrix = transformChain(
                            rotation(5, RotationPlane.AROUND_X, transform.rotationX),
                            rotation(5, RotationPlane.AROUND_Y, transform.rotationY),
                            rotation(5, RotationPlane.AROUND_Z, transform.rotationZ),
                            rotation(5, RotationPlane.XQ, transform.rotationQ),
                            translation(5, VectorN(
                                    transform.translationX,
                                    transform.translationY,
                                    transform.translationZ,
                                    transform.translationQ
                            ))
                    )
                    
                    val visualized = { point: VectorN ->
                        (point * modelMatrix).let {
                            if (isFourDimensional) when (fourthDimensionVisualizationMode) {
                                0 -> projectWireframe(it)
                                1 -> collapseZ(it)
                                else -> it
                            }
                            else it
                        }
                    }
                    
                    // C++:
                    drawGeometry(geometry, transform, color.encoded)
                    
                    // Iterate over double buffer, consuming one vector (4 doubles) per step:
                    (0 until positions.limit() step 4).map { index ->
                        
                        visualized(VectorN(
                                positions[index],
                                positions[index + 1],
                                positions[index + 2],
                                positions[index + 3]
                        ))
                        
                    }.map { transformedPosition ->
                        
                        // Bundle transformed position with geometry color,
                        // so that a complete vertex is created:
                        Vertex(transformedPosition, color)
                        
                    }.also {
                        vertexBuffer.draw(it)
                    }
                    
                }
                
                
            }
            
        }
        
    }
    
}
