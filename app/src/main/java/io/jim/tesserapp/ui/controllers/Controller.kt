package io.jim.tesserapp.ui.controllers

import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.math.common.formatNumber

/**
 * Control a specific value using a seek bar.
 *
 * @param update
 * Pipe the current value of the seek-bar to the actual value this controller targets.
 * It takes the current seek-bar value, mapped to range between [min] and [max].
 *
 */
class Controller(
        private val seeker: SeekBar,
        private val valueLabel: TextView,
        private val min: Double,
        private val max: Double,
        startValue: Double,
        private val formatString: String,
        update: (value: Double) -> Unit
) {

    /**
     * Maps the seeker progress onto the [min]-[max] range.
     */
    private var value = 0.0
        get() = seeker.progress.toFloat() / seeker.max * (max - min) + min

    init {
        if (max < min)
            throw RuntimeException("Maximum must be greater than minimum")
        if (startValue < min || startValue > max)
            throw RuntimeException("Start value must be located in min-max range")

        seeker.progress = ((startValue - min) / (max - min) * seeker.max).toInt()

        seeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // When a new value is received from the seek-bar, update both the text-view
                // as well as the internal value-receiver, which is implemented in the subclass:
                updateValueTextLabel()
                update(value)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Initially format the value label, using the start value:
        updateValueTextLabel()
        update(startValue)
    }

    /**
     * Update text of value-label according to the current seek-bar value.
     */
    private fun updateValueTextLabel() {
        valueLabel.text = String.format(formatString, formatNumber(value))
    }

}
