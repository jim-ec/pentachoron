package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

class RotationController(
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        private val setRotation: (rotation: Double) -> Unit
) : Controller(
        seeker = seeker,
        valueLabel = valueLabel,
        min = 0.0,
        max = 2.0,
        startValue = 0.0,
        formatString = context.getString(R.string.transform_rotation_value_radians)
) {

    override fun update(value: Double) {
        setRotation(value * Math.PI)
    }

}
