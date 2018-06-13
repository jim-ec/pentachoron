package io.jim.tesserapp.ui.model

import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.math.common.formatNumber
import io.jim.tesserapp.math.common.mapped

/**
 * Controller targeting a specific live data.
 *
 * @param monitor
 * The monitor this controller uses to safely access a [MainViewModel].
 *
 * @param seekBar
 * Seek bar to control the camera distance.
 *
 * @param watch
 * Text view representing the current camera distance.
 *
 * @param watchFormatString
 * String to format the watch.
 *
 * @param valueRange
 * Range to which the seek-bar progress is mapped.
 *
 * @param liveData
 * Returns the live data to be controlled.
 * When ran, the hosting view model is passed to it.
 *
 */
class Controller(
        val monitor: SynchronizedViewModel.Monitor,
        val liveData: MainViewModel.() -> MutableLiveDataNonNull<Double>,
        val seekBar: SeekBar,
        val watch: TextView,
        val valueRange: ClosedRange<Double>,
        val watchFormatString: String
) {

    /**
     * Range of possible seek-bar results.
     * The range is chosen in such a manner that you can chose values with a precision
     * of one tenth.
     */
    private val seekBarRange = 0.0..(valueRange.endInclusive - valueRange.start) * 10.0

    init {
        monitor<MainViewModel> {
            val startValue = liveData().value

            if (valueRange.isEmpty())
                throw RuntimeException("Controller value-range cannot be empty")
            if (!valueRange.contains(startValue))
                throw RuntimeException("Start value must be located in $valueRange")

            seekBar.progress = mapped(startValue, valueRange, seekBarRange).toInt()
            seekBar.max = seekBarRange.endInclusive.toInt()
        }

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
     * Updates the watch text and passes the new value to the live data.
     */
    private fun update() {
        val value = mapped(seekBar.progress.toDouble(), seekBarRange, valueRange)
        watch.text = String.format(watchFormatString, formatNumber(value))
        monitor<MainViewModel> {
            liveData().value = value
        }
    }

    /**
     * Unlink this controller from the seek-bar.
     *
     * You **must** call this function in order to disable a controller.
     */
    fun unlink() {
        seekBar.setOnSeekBarChangeListener(null)
    }

}
