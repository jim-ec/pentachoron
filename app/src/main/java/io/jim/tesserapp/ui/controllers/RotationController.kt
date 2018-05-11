package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R
import io.jim.tesserapp.math.common.Pi
import io.jim.tesserapp.math.common.formatNumber

class RotationController(
        controllables: List<Controllable>,
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        private val setRotation: (controllable: Controllable, rotation: Float) -> Unit
) : Controller(controllables, seeker, valueLabel, 0f, 2f) {

    private val formatString = context.getString(R.string.transform_rotation_value_radians)

    override fun set(controllable: Controllable, value: Float) {
        setRotation(controllable, value * Pi)
    }

    override val valueLabelText: String
        get() = String.format(formatString, formatNumber(currentSeekerValue))

}
