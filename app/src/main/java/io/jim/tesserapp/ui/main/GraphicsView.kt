/*
 *  Created by Jim Eckerlein on 7/17/18 5:16 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/17/18 5:09 PM
 */

package io.jim.tesserapp.ui.main

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.ScaleGestureDetector
import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.graphics.Renderer
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.util.consume
import io.jim.tesserapp.util.themedColorInt

/**
 * A view capable of rendering 3D geometry.
 */
class GraphicsView : GLSurfaceView {
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    
    val orbitDetector: MoveGestureDetector
    val zoomDetector: ScaleGestureDetector
    private val renderer: Renderer
    
    init {
        val viewModel = (context as MainActivity).viewModel
        
        orbitDetector = MoveGestureDetector(context, OrbitGestureListener(viewModel))
        zoomDetector = ScaleGestureDetector(context, ZoomGestureListener(viewModel))
        renderer = Renderer(
                Color(context.themedColorInt(android.R.attr.windowBackground)),
                viewModel,
                context.assets,
                resources.displayMetrics.xdpi.toDouble()
        )
        
        // Setup renderer:
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_CONTINUOUSLY
        
        setOnTouchListener { _, event ->
            consume {
                zoomDetector.onTouchEvent(event)
                orbitDetector.onTouchEvent(event)
            }
        }
    }
    
}
