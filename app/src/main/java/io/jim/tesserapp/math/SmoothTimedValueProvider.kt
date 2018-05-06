package io.jim.tesserapp.math

import android.os.Handler
import android.os.Looper
import java.util.*

/**
 * Delegates a smoothed value property.
 * @property onChanged Called when value changed.
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

        override fun run() {
            handler.post {
                val valueNow = currentValue
                if (valueNow != oldValue) {
                    onChanged(valueNow)
                    oldValue = valueNow
                }
            }
        }

    }, 0L, timerInterval)
}
