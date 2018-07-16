/*
 *  Created by Jim Eckerlein on 7/16/18 1:56 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/16/18 1:56 PM
 */

package io.jim.tesserapp.ui.main

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.GestureDetector
import io.jim.tesserapp.graphics.Renderer
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.themedColorInt

/**
 * A view capable of rendering 3D geometry.
 */
class GraphicsView : GLSurfaceView {
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    
    private val viewModel = (context as MainActivity).viewModel
    
    val detector = GestureDetector(context, MainGestureListener(viewModel))
    
    private val renderer = Renderer(
            Color(context.themedColorInt(android.R.attr.windowBackground)),
            viewModel,
            context.assets,
            resources.displayMetrics.xdpi.toDouble()
    )
    
    init {
        
        // Setup renderer:
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_CONTINUOUSLY
    
        setOnTouchListener { _, event ->
            if (detector.onTouchEvent(event)) {
                // Gesture detector consumed the event, so does the listener:
                CONSUMED
            } else {
                // Gesture detector was unable to consume the event, forward it:
                super.onTouchEvent(event)
            }
        }
    }
    
}
