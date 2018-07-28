/*
 *  Created by Jim Eckerlein on 7/28/18 7:39 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/28/18 7:39 PM
 */

package io.jim.tesserapp.math

import android.graphics.PointF
import androidx.core.graphics.minus
import io.jim.tesserapp.util.UiCoroutineContext
import io.jim.tesserapp.util.consume
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import java.security.InvalidParameterException

/**
 * Consumes scroll events, while spitting out an approximating point at a fixed time rate.
 */
class ScrollAttractor(
        val approximationCoefficient: Float,
        val onApproximated: (deltaApproximation: PointF) -> Unit
) {
    
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
    
    fun scrollTo(x: Float, y: Float) = consume {
        attractor.x = x
        attractor.y = y
    }
    
    fun resetScrollTo(x: Float, y: Float) = consume {
        listOf(attractor, approximator, previousApproximator).forEach {
            it.x = x
            it.y = y
        }
    }
    
}
