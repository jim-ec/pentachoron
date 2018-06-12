package io.jim.tesserapp.ui.model

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * A translation controller.
 * The current translation value is passed to [onTranslated].
 */
inline fun <reified T : SynchronizedViewModel> translationController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        startValue: Double,
        crossinline onTranslated: (viewModel: T, translation: Double) -> Unit,
        viewModel: T
): Controller = run {

    val viewModelMonitor = viewModel.monitor<T>()

    Controller(
            seekBar = seekBar,
            watch = watch,
            valueRange = -5.0..5.0,
            startValue = startValue,
            formatString = context.getString(R.string.transform_translation_watch_format),
            onValueUpdate = { value ->
                viewModelMonitor { viewModel ->
                    onTranslated(viewModel, value)
                }
            })
}
