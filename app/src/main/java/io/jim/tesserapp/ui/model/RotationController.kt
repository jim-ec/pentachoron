package io.jim.tesserapp.ui.model

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * Controller targeting the rotation, in units of Pi.
 *
 * @receiver
 * The view model containing the targeting live data.
 * Need not to be externally synchronized, as that's done internally.
 *
 * @param context
 * App context.
 *
 * @param seekBar
 * Seek bar to control the camera distance.
 *
 * @param watch
 * Text view representing the current camera distance.
 *
 * @param liveData
 * Runs on the receiving view model.
 * Returns the live data to be controlled.
 */
inline fun MainViewModel.rotationController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        crossinline liveData: MainViewModel.() -> MutableLiveDataNonNull<Double>
): Controller = run {

    // Monitor used when accessing the view model in a synchronized manner:
    val monitor = Monitor()

    Controller(
            seekBar = seekBar,
            watch = watch,
            valueRange = 0.0..2.0,
            startValue = monitor { viewModel: MainViewModel ->
                viewModel.liveData().value
            },
            formatString = context.getString(R.string.transform_rotation_watch_format),
            onValueUpdate = { value ->
                monitor { viewModel: MainViewModel ->
                    liveData(viewModel).value = value
                }
            }
    )
}
