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
        crossinline liveData: T.() -> MutableLiveDataNonNull<Double>
): Controller = run {

    val viewModelMonitor = viewModel.Monitor()

    Controller(
            seekBar = seekBar,
            watch = watch,
            valueRange = 3.0..15.0,
            startValue = viewModelMonitor { viewModel: T ->
                viewModel.liveData().value
            },
            formatString = context.getString(R.string.camera_distance_watch_format),
            onValueUpdate = { distance ->
                viewModelMonitor { viewModel: T ->
                    liveData(viewModel).value = distance
                }
            }
    )
}
