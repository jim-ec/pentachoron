package io.jim.tesserapp.ui.model

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * Controller targeting the camera distance.
 */

inline fun <reified T : SynchronizedViewModel> cameraDistanceController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        viewModel: T,
        crossinline liveData: (viewModel: T) -> MutableLiveDataNonNull<Double>
): Controller = run {

    val viewModelMonitor = viewModel.monitor<T>()

    Controller(
            seekBar = seekBar,
            watch = watch,
            valueRange = 3.0..15.0,
            startValue = liveData(viewModel).value,
            formatString = context.getString(R.string.camera_distance_watch_format),
            onValueUpdate = { distance ->
                viewModelMonitor { _ ->
                    liveData(viewModel).value = distance
                }
            }
    )
}

/*
inline fun <reified T : SynchronizedViewModel> cameraDistanceController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        startValue: Double,
        crossinline onDistanceChanged: (viewModel: T, distance: Double) -> Unit,
        viewModel: T
): Controller = run {

    val viewModelMonitor = viewModel.monitor<T>()

    Controller(
            seekBar = seekBar,
            watch = watch,
            valueRange = 3.0..15.0,
            startValue = startValue,
            formatString = context.getString(R.string.camera_distance_watch_format),
            onValueUpdate = { distance ->
                viewModelMonitor { viewModel ->
                    onDistanceChanged(viewModel, distance)
                }
            }
    )
}
*/