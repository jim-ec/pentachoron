package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * Create a rotation controller.
 * While the internal value is kept between 0.0 and 2.0, the updater function passes that value
 * multiplied by two pi to [setRotation].
 */
fun rotationController(
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        setRotation: (rotation: Double) -> Unit
) = Controller(
        seekBar = seeker,
        watch = valueLabel,
        min = 0.0,
        max = 2.0,
        startValue = 0.0,
        formatString = context.getString(R.string.transform_rotation_value_radians),
        onValueUpdate = { value ->
            setRotation(value * Math.PI)
        }
)
