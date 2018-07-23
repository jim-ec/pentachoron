/*
 *  Created by Jim Eckerlein on 7/23/18 9:34 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/23/18 9:34 AM
 */

package io.jim.tesserapp.ui.main

import android.view.GestureDetector
import android.view.MotionEvent
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume

class SwipeAreaGestureListener(val viewModel: MainViewModel) :
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
    
        synchronized(viewModel) {
            when (viewModel.transformMode.value) {
        
                TransformMode.ROTATE ->
    
                    when (viewModel.selectedAxis.value) {
                        SelectedAxis.X -> viewModel.rotationX
                        SelectedAxis.Y -> viewModel.rotationY
                        SelectedAxis.Z -> viewModel.rotationZ
                        SelectedAxis.Q -> viewModel.rotationQ
                    }.value += 0.1 * (distanceX / (5 * timeDelta))
        
                TransformMode.TRANSLATE ->
    
                    when (viewModel.selectedAxis.value) {
                        SelectedAxis.X -> viewModel.translationX
                        SelectedAxis.Y -> viewModel.translationY
                        SelectedAxis.Z -> viewModel.translationZ
                        SelectedAxis.Q -> viewModel.translationQ
                    }.value += 0.4 * (distanceX / timeDelta)
            }
        }
    }
    
    override fun onDoubleTap(e: MotionEvent?) = consume {
        synchronized(viewModel) {
            // Toggle between translate and rotate:
            viewModel.transformMode.value = when (viewModel.transformMode.value) {
                TransformMode.ROTATE -> TransformMode.TRANSLATE
                TransformMode.TRANSLATE -> TransformMode.ROTATE
            }
        }
    }
    
}
