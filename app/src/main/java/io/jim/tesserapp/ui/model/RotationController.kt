package io.jim.tesserapp.ui.model

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * Create a rotation controller.
 * While the internal value is kept between 0.0 and 2.0, the updater function passes that value
 * multiplied by two pi to [onRotated].
 */
inline fun <reified T : SynchronizedViewModel> rotationController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        startValue: Double,
        crossinline onRotated: (viewModel: T, rotation: Double) -> Unit,
        viewModel: T
): Controller = run {

    val viewModelMonitor = viewModel.monitor<T>()

    Controller(
            seekBar = seekBar,
            watch = watch,
            valueRange = 0.0..2.0,
            startValue = startValue,
            formatString = context.getString(R.string.transform_rotation_watch_format),
            onValueUpdate = { value ->
                viewModelMonitor { viewModel ->
                    onRotated(viewModel, value)
                }
            })
}
