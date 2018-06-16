package io.jim.tesserapp.ui.model

import io.jim.tesserapp.math.common.Smoothed

/**
 * Live data with an additional member smoothly interpolating between data-changes.
 * @param initialValue Initial value of live data.
 * @param transitionInterval The time it should take to fulfil one transition interval.
 * @param delegationMode Assigned to [smoothed]'s [Smoothed.delegationMode].
 */
class SmoothedLiveData(
        initialValue: Double = 0.0,
        delegationMode: Smoothed.DelegationMode = Smoothed.DelegationMode.ABSOLUTE,
        transitionInterval: Double = 200.0
) : MutableLiveDataNonNull<Double>(initialValue) {
    
    /**
     * Reflect the live-data's value, but interpolates smoothly between changes.
     * You cannot set this smoothed *variant* directly since it's automatically
     * set when the actual, non-smoothed live data is changed.
     */
    var smoothed by Smoothed(initialValue, transitionInterval, delegationMode)
        private set
    
    init {
        observeForeverNonNull {
            smoothed = it
        }
    }
    
}
