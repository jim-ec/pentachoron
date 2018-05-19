package io.jim.tesserapp.ui.controllers

import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.math.common.formatNumber

/**
 * Control a specific value using a seek bar.
 */
abstract class Controller(
        private val seeker: SeekBar,
        private val valueLabel: TextView,
        private val min: Double,
        private val max: Double,
        startValue: Double = min,
        private val formatString: String
) {

    protected abstract fun set(value: Double)

    /**
     * Maps the seeker progress onto the [min]-[max] range.
     */
    protected var currentSeekerValue = 0.0
        get() = seeker.progress.toFloat() / seeker.max * (max - min) + min

    /**
     * The current value formatted into a string.
     */
    private fun valueLabelText(value: Double) =
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
        currentSeekerValue.also {
            valueLabel.text = valueLabelText(it)
            set(it)
        }
    }

}
