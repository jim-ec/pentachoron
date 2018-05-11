package io.jim.tesserapp.ui.controllers

import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.math.common.formatNumber

/**
 * Control a single value, targeting single value of [Controllable].
 */
abstract class Controller(
        private var controllables: List<Controllable>,
        private val seeker: SeekBar,
        private val valueLabel: TextView,
        private val min: Float,
        private val max: Float,
        startValue: Float = min,
        private val formatString: String
) {
    /**
     * Whenever the controlled value changed, this function is called for
     * each [controllable] registered at this time-point with the new [value].
     */
    protected abstract fun set(controllable: Controllable, value: Float)

    /**
     * Maps the seeker progress onto the [min]-[max] range.
     */
    protected var currentSeekerValue = 0f
        get() = seeker.progress.toFloat() / seeker.max * (max - min) + min

    /**
     * The current value formatted into a string.
     */
    private fun valueLabelText(value: Float) =
            String.format(formatString, formatNumber(value))

    init {
        if (max < min)
            throw RuntimeException("Maximum must be greater than minimum")
        if (startValue < min || startValue > max)
            throw RuntimeException("Start value must be located in min-max range")

        seeker.progress = ((startValue - min) / (max - min) * seeker.max).toInt()

        seeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                reevaluate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    fun reevaluate() {
        val value = currentSeekerValue
        valueLabel.text = valueLabelText(value)

        setControlledValue(value)

        controllables.forEach {
            set(it, value)
        }
    }

    // TODO: Make abstract
    open fun setControlledValue(value: Float) {}

}
