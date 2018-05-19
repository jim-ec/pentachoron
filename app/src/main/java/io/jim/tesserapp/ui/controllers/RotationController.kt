package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R
import io.jim.tesserapp.math.common.Pi

class RotationController(
        controllables: List<Controllable>,
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        private val setRotation: (controllable: Controllable, rotation: Double) -> Unit
) : Controller(
        controllables,
        seeker,
        valueLabel,
        0.0,
        2.0,
        0.0,
        context.getString(R.string.transform_rotation_value_radians)
) {

    override fun set(controllable: Controllable, value: Double) {
        setRotation(controllable, value * Pi)
    }

}
