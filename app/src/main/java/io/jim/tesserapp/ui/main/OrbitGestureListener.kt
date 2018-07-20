/*
 *  Created by Jim Eckerlein on 7/17/18 5:16 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/17/18 5:14 PM
 */

package io.jim.tesserapp.ui.main

import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume
import io.jim.tesserapp.util.synchronized

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
        viewModel.synchronized {
            horizontalCameraRotation.value += detector.focusDelta.x * ROTATION_SENSITIVITY
            verticalCameraRotation.value -= detector.focusDelta.y * ROTATION_SENSITIVITY
        }
    }
    
    override fun onMoveEnd(detector: MoveGestureDetector?) {}
    
}
