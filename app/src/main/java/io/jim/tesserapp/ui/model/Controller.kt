package io.jim.tesserapp.ui.model

import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.math.common.formatNumber
import io.jim.tesserapp.math.common.mapped

/**
 * Control a specific value using a seek bar.
 *
 * @param onValueUpdate
 * Pipe the current value of the seek-bar to the actual value this controller targets.
 * It takes the current seek-bar value, mapped to range between [min] and [max].
 *
 * @property seekBar
 * The seek-bar as the UI value-input.
 *
 * @property watch
 * The text-view displaying the current value.
 *
 */
class Controller(
        private val seekBar: SeekBar,
        private val watch: TextView,
        private val valueRange: ClosedRange<Double>,
        startValue: Double,
        private val formatString: String,
        private val onValueUpdate: (value: Double) -> Unit
) {

    /**
     * Range of possible seek-bar results.
     * The range is chosen in such a manner that you can chose values with a precision
     * of one tenth.
     */
    private val seekBarRange = 0.0..(valueRange.endInclusive - valueRange.start) * 10.0

    init {
        if (valueRange.isEmpty())
            throw RuntimeException("Controller value-range cannot be empty")
        if (!valueRange.contains(startValue))
            throw RuntimeException("Start value must be located in $valueRange")

        seekBar.progress = mapped(startValue, valueRange, seekBarRange).toInt()
        seekBar.max = seekBarRange.endInclusive.toInt()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                update()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Initially format the value label, using the start value:
        update()
    }

    /**
     * Called when seek-bar progress changes.
     * Updates the watch text and calls [onValueUpdate] with the new value.
     */
    private fun update() {
        val value = mapped(seekBar.progress.toDouble(), seekBarRange, valueRange)
        watch.text = String.format(formatString, formatNumber(value))
        onValueUpdate(value)
    }

}
