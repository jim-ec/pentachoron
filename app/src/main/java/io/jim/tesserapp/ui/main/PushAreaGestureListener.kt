/*
 *  Created by Jim Eckerlein on 7/22/18 12:28 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/22/18 12:10 PM
 */

package io.jim.tesserapp.ui.main

import android.view.GestureDetector
import android.view.MotionEvent
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume
import io.jim.tesserapp.util.synchronized

class PushAreaGestureListener(val viewModel: MainViewModel) :
        GestureDetector.SimpleOnGestureListener() {
    
    private var previousEventTime = 0L
    private var transformMode = TransformMode.ROTATE
    
    override fun onDown(e: MotionEvent?) = CONSUMED
    
    override fun onScroll(
            startEvent: MotionEvent?,
            endEvent: MotionEvent?,
            distanceX: Float,
            distanceY: Float
    ) = consume {
        endEvent ?: return NOT_CONSUMED
        val timeDelta = endEvent.eventTime - previousEventTime
        previousEventTime = endEvent.eventTime
        if (timeDelta <= 0) return NOT_CONSUMED
        
        viewModel.synchronized {
            if (transformMode == TransformMode.ROTATE) {
                
                when (selectedAxis) {
                    SelectedAxis.X -> rotationX
                    SelectedAxis.Y -> rotationY
                    SelectedAxis.Z -> rotationZ
                    SelectedAxis.Q -> rotationQ
                }.value += 0.1 * (distanceX / (5 * timeDelta))
                
            } else if (transformMode == TransformMode.TRANSLATE) {
                
                when (selectedAxis) {
                    SelectedAxis.X -> translationX
                    SelectedAxis.Y -> translationY
                    SelectedAxis.Z -> translationZ
                    SelectedAxis.Q -> translationQ
                }.value += 0.4 * (distanceX / timeDelta)
                
            }
        }
    }
    
    override fun onDoubleTap(e: MotionEvent?) = consume {
        transformMode = when (transformMode) {
            TransformMode.ROTATE -> TransformMode.TRANSLATE
            TransformMode.TRANSLATE -> TransformMode.ROTATE
        }
    }
    
}
