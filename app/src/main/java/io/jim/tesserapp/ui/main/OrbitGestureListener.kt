/*
 *  Created by Jim Eckerlein on 7/23/18 9:34 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/23/18 9:28 AM
 */

package io.jim.tesserapp.ui.main

import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume

class OrbitGestureListener(val viewModel: MainViewModel) : MoveGestureDetector.OnMoveGestureListener {
    
    companion object {
        private const val ROTATION_SENSITIVITY = 0.005
    }
    
    override fun onMoveBegin(detector: MoveGestureDetector?) = CONSUMED
    
    /**
     * Moving orbits the camera around.
     */
    override fun onMove(detector: MoveGestureDetector?) = consume {
        detector ?: return NOT_CONSUMED
        synchronized(viewModel) {
            viewModel.horizontalCameraRotation.value += detector.focusDelta.x * ROTATION_SENSITIVITY
            viewModel.verticalCameraRotation.value -= detector.focusDelta.y * ROTATION_SENSITIVITY
        }
    }
    
    override fun onMoveEnd(detector: MoveGestureDetector?) {}
    
}
