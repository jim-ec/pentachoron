/*
 *  Created by Jim Eckerlein on 7/30/18 9:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/30/18 9:03 PM
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
 *
 * @property halfLife
 * The time it takes the approximator to reach half of the distance to the target point.
 * E.g. if set to `50`, the approximator will cover half of its *current* distance
 * to the target point every 50 milliseconds. To be clear, the approximator will *never*
 * completely reach the target, since the distance is always halved at a fixed time rate.
 *
 * Caution must be taken when choosing a very small half-life.
 * If the half-life is smaller than the time rate at which the approximator position is
 * requested, the approximator will begin to oscillate around the target point.
 */
class ScrollAttractor(
        val halfLife: Long,
        val onApproximated: (deltaApproximation: PointF) -> Unit
) {
    
    val attractor = PointF()
    val approximator = PointF()
    val previousApproximator = PointF()
    var t0 = 0L
    
    init {
        if (halfLife <= 0L) {
            throw InvalidParameterException("Invalid half-life " +
                    "$halfLife, must be a non-zero, positive value")
        }
    
        launch(UiCoroutineContext) {
            while (true) {
    
                val t = System.currentTimeMillis()
    
                val factor = 0.5 * (t - t0).toDouble() / halfLife
                approximator.x += (attractor.x - approximator.x) * factor.toFloat()
                approximator.y += (attractor.y - approximator.y) * factor.toFloat()
    
                t0 = t
            
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
