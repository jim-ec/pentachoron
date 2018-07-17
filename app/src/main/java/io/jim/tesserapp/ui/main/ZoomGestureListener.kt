/*
 *  Created by Jim Eckerlein on 7/17/18 5:16 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/17/18 5:12 PM
 */

package io.jim.tesserapp.ui.main

import android.view.ScaleGestureDetector
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume


class ZoomGestureListener(val viewModel: MainViewModel) : ScaleGestureDetector.OnScaleGestureListener {
    
    override fun onScaleBegin(detector: ScaleGestureDetector?) = CONSUMED
    
    override fun onScale(detector: ScaleGestureDetector?) = consume {
        detector ?: return NOT_CONSUMED
        
        // A scale factor > 1 means that the pointer distance increased, i.e. zooming out:
        viewModel.cameraDistance.value /= detector.scaleFactor
        
        // Constrain camera distance to a certain minimum:
        viewModel.cameraDistance.value = Math.max(viewModel.cameraDistance.value, 2.0)
    }
    
    override fun onScaleEnd(detector: ScaleGestureDetector?) {}
    
}
