/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.ui.main

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import io.jim.tesserapp.graphics.Renderer
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.themedColorInt

/**
 * A view capable of rendering 3D geometry.
 */
class GraphicsView : GLSurfaceView {
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    
    private val viewModel = (context as MainActivity).viewModel
    
    private val renderer = Renderer(
            Color(context.themedColorInt(android.R.attr.windowBackground)),
            viewModel,
            context.assets,
            resources.displayMetrics.xdpi.toDouble()
    )
    
    private var touchStartPositionX = 0f
    private var touchStartPositionY = 0f
    private var touchStartTime = 0L
    
    companion object {
        private const val CLICK_TIME_MS = 100L
        private const val TOUCH_ROTATION_SENSITIVITY = 0.008
    }
    
    init {
        
        // Setup renderer:
        setEGLContextClientVersion(2)
        setRenderer(renderer)
        debugFlags = DEBUG_CHECK_GL_ERROR
        renderMode = RENDERMODE_CONTINUOUSLY
    }
    
    /**
     * Handles camera orbit position upon touch events.
     */
    override fun onTouchEvent(event: MotionEvent?) =
            if (null == event) false
            else when {
                event.action == ACTION_DOWN -> {
                    touchStartPositionX = event.x
                    touchStartPositionY = event.y
                    touchStartTime = System.currentTimeMillis()
                    CONSUMED
                }
                event.action == ACTION_MOVE -> {
                    val dx = event.x - touchStartPositionX
                    val dy = event.y - touchStartPositionY
                    
                    viewModel.horizontalCameraRotation.value += dx * TOUCH_ROTATION_SENSITIVITY
                    viewModel.verticalCameraRotation.value -= dy * TOUCH_ROTATION_SENSITIVITY
                    
                    touchStartPositionX = event.x
                    touchStartPositionY = event.y
                    CONSUMED
                }
                event.action == ACTION_UP
                        && System.currentTimeMillis() - touchStartTime < CLICK_TIME_MS -> {
                    performClick()
                    CONSUMED
                }
                else -> NOT_CONSUMED
            }
    
    override fun performClick(): Boolean {
        super.performClick()
        return CONSUMED
    }
    
}
