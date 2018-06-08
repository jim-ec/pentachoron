package io.jim.tesserapp.ui.model

import io.jim.tesserapp.math.common.Smoothed

/**
 * Live data with an additional member smoothly interpolating between data-changes.
 * @param initialValue Initial value of live data.
 * @param transitionInterval The time it should take to fulfil one transition interval.
 * @param delegateDifference Assigned to [smoothed]'s [Smoothed.delegateDifference].
 */
class SmoothedLiveData(
        initialValue: Double,
        transitionInterval: Double = 200.0,
        delegateDifference: Boolean = true
) : MutableLiveDataNonNull<Double>(initialValue) {

    /**
     * Reflect the live-data's value, but interpolates smoothly between changes.
     */
    var smoothed by Smoothed(initialValue, transitionInterval, delegateDifference)
        private set

    init {
        observeForeverNonNull {
            smoothed = it
        }
    }

}
