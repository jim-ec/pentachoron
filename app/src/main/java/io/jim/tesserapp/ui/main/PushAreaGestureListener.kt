/*
 *  Created by Jim Eckerlein on 7/22/18 4:27 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/22/18 4:27 PM
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
            when (transformMode.value) {
        
                TransformMode.ROTATE ->
    
                    when (selectedAxis.value) {
                        SelectedAxis.X -> rotationX
                        SelectedAxis.Y -> rotationY
                        SelectedAxis.Z -> rotationZ
                        SelectedAxis.Q -> rotationQ
                    }.value += 0.1 * (distanceX / (5 * timeDelta))
        
                TransformMode.TRANSLATE ->
    
                    when (selectedAxis.value) {
                        SelectedAxis.X -> translationX
                        SelectedAxis.Y -> translationY
                        SelectedAxis.Z -> translationZ
                        SelectedAxis.Q -> translationQ
                    }.value += 0.4 * (distanceX / timeDelta)
            }
        }
    }
    
    override fun onDoubleTap(e: MotionEvent?) = consume {
        viewModel.synchronized {
            // Toggle between translate and rotate:
            transformMode.value = when (transformMode.value) {
                TransformMode.ROTATE -> TransformMode.TRANSLATE
                TransformMode.TRANSLATE -> TransformMode.ROTATE
            }
        }
    }
    
}
