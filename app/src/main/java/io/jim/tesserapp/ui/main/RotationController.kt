package io.jim.tesserapp.ui.main

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
fun MainViewModel.rotationController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        liveData: MainViewModel.() -> MutableLiveDataNonNull<Double>
) = Controller(
        viewModel = this,
        liveData = liveData,
        seekBar = seekBar,
        watch = watch,
        watchFormatString = context.getString(R.string.transform_rotation_watch_format),
        valueRange = 0.0..2.0
)
