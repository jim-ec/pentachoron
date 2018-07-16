/*
 *  Created by Jim Eckerlein on 7/16/18 12:39 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/16/18 12:36 PM
 */

package io.jim.tesserapp.ui.main

import android.view.GestureDetector
import android.view.MotionEvent
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.consume


class MainGestureListener(
        val viewModel: MainViewModel
) : GestureDetector.SimpleOnGestureListener() {
    
    companion object {
        private const val ROTATION_SENSITIVITY = 0.008
    }
    
    /**
     * Consume every incoming gesture.
     * The default implementation cancels everything by returning `false` always,
     * i.e. no other gesture callback is every called.
     */
    override fun onDown(event: MotionEvent?) = CONSUMED
    
    /**
     * Scroll events orbit the camera around.
     */
    override fun onScroll(
            startEvent: MotionEvent?,
            endEvent: MotionEvent?,
            dx: Float,
            dy: Float
    ) = consume {
        viewModel.horizontalCameraRotation.value -= dx * ROTATION_SENSITIVITY
        viewModel.verticalCameraRotation.value += dy * ROTATION_SENSITIVITY
    }
    
}
