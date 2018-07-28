/*
 *  Created by Jim Eckerlein on 7/28/18 10:25 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/28/18 10:25 AM
 */

package io.jim.tesserapp.math

import android.graphics.PointF
import androidx.core.graphics.minus
import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.UiCoroutineContext
import io.jim.tesserapp.util.consume
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.security.InvalidParameterException

class AttractionGestureListener(
        val approximationCoefficient: Float,
        val onApproximated: (deltaApproximation: PointF) -> Unit
) : MoveGestureDetector.SimpleOnMoveGestureListener() {
    
    val attractor = PointF()
    val approximator = PointF()
    val previousApproximator = PointF()
    
    init {
        if (approximationCoefficient <= 0f || approximationCoefficient > 1f) {
            throw InvalidParameterException("Invalid approximation coefficient " +
                    "$approximationCoefficient, must be in ]0;1]")
        }
    
        launch(UiCoroutineContext) {
            while (true) {
                approximator.x += (attractor.x - approximator.x) * approximationCoefficient
                approximator.y += (attractor.y - approximator.y) * approximationCoefficient
            
                onApproximated(approximator - previousApproximator)
                
                previousApproximator.x = approximator.x
                previousApproximator.y = approximator.y
            
                delay(17)
            }
        }
    }
    
    override fun onMoveBegin(detector: MoveGestureDetector?) = consume {
        detector ?: return NOT_CONSUMED
        attractor.x = detector.focusX
        attractor.y = detector.focusY
        approximator.x = detector.focusX
        approximator.y = detector.focusY
        previousApproximator.x = detector.focusX
        previousApproximator.y = detector.focusY
    }
    
    override fun onMove(detector: MoveGestureDetector?) = consume {
        detector ?: return NOT_CONSUMED
        attractor.x = detector.focusX
        attractor.y = detector.focusY
    }
    
}
