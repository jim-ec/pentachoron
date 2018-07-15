/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.ui.main

import io.jim.tesserapp.math.Smoothed

/**
 * Live data with an additional member smoothly interpolating between data-changes.
 * @param initialValue Initial value of live data.
 * @param transitionInterval The time it should take to fulfil one transition interval.
 */
class SmoothedLiveData(
        initialValue: Double = 0.0,
        transitionInterval: Double = 200.0
) : MutableLiveDataNonNull<Double>(initialValue) {
    
    /**
     * Reflect the live-data's value, but interpolates smoothly between changes.
     * You cannot set this smoothed *variant* directly since it's automatically
     * set when the actual, non-smoothed live data is changed.
     */
    var smoothed by Smoothed(initialValue, transitionInterval)
        private set
    
    init {
        observeForeverNonNull {
            smoothed = it
        }
    }
    
}
