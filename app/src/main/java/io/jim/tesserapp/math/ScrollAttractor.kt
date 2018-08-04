/*
 *  Created by Jim Eckerlein on 8/4/18 10:03 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 8/4/18 10:00 PM
 */

package io.jim.tesserapp.math

import android.graphics.PointF
import io.jim.tesserapp.util.consume
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
class ScrollAttractor(val halfLife: Long) {
    
    private val attractor = PointF()
    private var t0 = 0L
    private val approximator = PointF()
    
    init {
        if (halfLife <= 0L) {
            throw InvalidParameterException("Invalid half-life " +
                    "$halfLife, must be a non-zero, positive value")
        }
    }
    
    fun scrollTo(x: Float, y: Float) = consume {
        attractor.x = x
        attractor.y = y
    }
    
    fun resetScrollTo(x: Float, y: Float) = consume {
        listOf(attractor, approximator).forEach {
            it.x = x
            it.y = y
        }
    }
    
    fun computeNextApproximation(): PointF {
        val t = System.currentTimeMillis()
        
        val factor = 0.5 * (t - t0).toDouble() / halfLife
        approximator.x += (attractor.x - approximator.x) * factor.toFloat()
        approximator.y += (attractor.y - approximator.y) * factor.toFloat()
        
        t0 = t
        
        return PointF(approximator.x, approximator.y)
    }
    
    /**
     * Stops the approximator moving towards the attractor,
     * keeping its current position.
     * This will effectively move the attractor to the approximator's
     * current position.
     */
    fun haltApproximation() {
        attractor.x = approximator.x
        attractor.y = approximator.y
    }
    
}
