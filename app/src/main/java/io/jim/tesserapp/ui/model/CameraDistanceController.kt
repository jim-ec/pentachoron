package io.jim.tesserapp.ui.model

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * Controller targeting the camera distance.
 *
 * @receiver
 * The view model containing the [MainViewModel.cameraDistance].
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
 */
fun MainViewModel.cameraDistanceController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView
) = Controller(
        hostingViewModel = this,
        liveData = { cameraDistance },
        seekBar = seekBar,
        watch = watch,
        watchFormatString = context.getString(R.string.camera_distance_watch_format),
        valueRange = 3.0..15.0
)
