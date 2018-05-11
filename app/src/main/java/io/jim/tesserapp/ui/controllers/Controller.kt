package io.jim.tesserapp.ui.controllers

import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.math.common.SmoothTimedValueProvider

/**
 * Control a single value, targeting single value of [Controllable].
 */
abstract class Controller(
        private var controllables: List<Controllable>,
        private val seeker: SeekBar,
        private val valueLabel: TextView,
        private val min: Float,
        private val max: Float,
        startValue: Float = min
) {
    /**
     * Whenever the controlled value changed, this function is called for
     * each [controllable] registered at this time-point with the new [value].
     */
    protected abstract fun set(controllable: Controllable, value: Float)

    /**
     * Maps the seeker progress onto the [min]-[max] range.
     */
    protected var currentSeekerValue by SmoothTimedValueProvider<Controller>(
            startValue = 0f,
            transitionTimeInterval = 300L,
            timerInterval = 10L
    ) {
        reevaluate()
    }

    /**
     * The current value formatted into a string.
     */
    protected abstract val valueLabelText: String

    init {
        if (max < min)
            throw RuntimeException("Maximum must be greater than minimum")
        if (startValue < min || startValue > max)
            throw RuntimeException("Start value must be located in min-max range")

        seeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentSeekerValue = seeker.progress.toFloat() / seeker.max * (max - min) + min
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Force the seeker bar to at least once call its value-changed listeners:
        seeker.progress = 1
        seeker.progress = ((startValue - min) / (max - min) * seeker.max).toInt()
    }

    fun reevaluate() {
        valueLabel.text = valueLabelText
        controllables.forEach {
            set(it, currentSeekerValue)
        }
    }
}
