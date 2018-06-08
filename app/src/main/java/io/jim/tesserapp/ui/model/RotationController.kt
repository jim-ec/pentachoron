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
fun rotationController(
        context: Context,
        seekBar: SeekBar,
        watch: TextView,
        startValue: Double,
        onRotated: (rotation: Double) -> Unit
) = Controller(
        seekBar = seekBar,
        watch = watch,
        valueRange = 0.0..2.0,
        startValue = startValue,
        formatString = context.getString(R.string.transform_rotation_watch_format),
        onValueUpdate = onRotated
)
