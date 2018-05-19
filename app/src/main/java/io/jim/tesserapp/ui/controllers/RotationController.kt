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
        seeker,
        valueLabel,
        0.0,
        2.0,
        0.0,
        context.getString(R.string.transform_rotation_value_radians)
) {

    override fun set(value: Double) {
        setRotation(value * Math.PI)
    }

}
