/*
 *  Created by Jim Eckerlein on 7/24/18 4:48 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/24/18 4:48 PM
 */

package io.jim.tesserapp.math

import android.graphics.PointF
import androidx.core.graphics.minus
import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume
import java.security.InvalidParameterException
import java.util.*
import kotlin.concurrent.schedule

class AttractionGestureListener(
        val approximationCoefficient: Float,
        val callback: (approximator: PointF) -> Unit
) : MoveGestureDetector.SimpleOnMoveGestureListener() {
    
    val attractor = PointF()
    val approximator = PointF()
    val previousApproximator = PointF()
    
    init {
        if (approximationCoefficient <= 0f || approximationCoefficient > 1f) {
            throw InvalidParameterException("Invalid approximation coefficient " +
                    "$approximationCoefficient, must be in ]0;1]")
        }
        
        Timer().schedule(delay = 0L, period = 17L) {
            synchronized(this@AttractionGestureListener) {
                approximator.x += (attractor.x - approximator.x) * approximationCoefficient
                approximator.y += (attractor.y - approximator.y) * approximationCoefficient
                
                callback(approximator - previousApproximator)
                
                previousApproximator.x = approximator.x
                previousApproximator.y = approximator.y
            }
        }
    }
    
    override fun onMoveBegin(detector: MoveGestureDetector?) = consume {
        detector ?: return NOT_CONSUMED
        
        synchronized(this) {
            attractor.x = detector.focusX
            attractor.y = detector.focusY
            approximator.x = detector.focusX
            approximator.y = detector.focusY
            previousApproximator.x = detector.focusX
            previousApproximator.y = detector.focusY
        }
    }
    
    override fun onMove(detector: MoveGestureDetector?) = consume {
        detector ?: return NOT_CONSUMED
        
        synchronized(this) {
            attractor.x = detector.focusX
            attractor.y = detector.focusY
        }
        
        
    }
    
}
