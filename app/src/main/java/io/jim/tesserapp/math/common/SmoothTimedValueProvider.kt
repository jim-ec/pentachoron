package io.jim.tesserapp.math.common

import android.os.Handler
import android.os.Looper
import java.util.*

/**
 * Delegates a smoothed value property and additionally calls a callback whenever the value changed.
 * The timer callback function [onChanged] is called on the main thread.
 *
 * @param startValue Start value of property.
 * @param transitionTimeInterval Time it takes to reach a new value.
 * @param timerInterval The time step at which the value is re-checked for having a new value.
 * @param onChanged Called when value changed.
 */
class SmoothTimedValueProvider<R>(
        startValue: Float,
        transitionTimeInterval: Long,
        timerInterval: Long,
        private val onChanged: (value: Float) -> Unit
) : SmoothTimedValueDelegate<R>(startValue, transitionTimeInterval) {

    var oldValue = startValue

    private val timer = Timer().scheduleAtFixedRate(object : TimerTask() {
        val handler = Handler(Looper.getMainLooper())

        val checkForNewValue = Runnable {
            val valueNow = currentValue
            if (valueNow != oldValue) {
                onChanged(valueNow)
                oldValue = valueNow
            }
        }

        override fun run() {
            handler.post(checkForNewValue)
        }

    }, 0L, timerInterval)
}
