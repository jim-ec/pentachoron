package io.jim.tesserapp.ui

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R
import io.jim.tesserapp.math.common.formatNumber

class CameraDistanceController(
        controllables: List<Controllable>,
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        min: Float = 3f,
        max: Float = 15f,
        private val setDistance: (controllable: Controllable, distance: Float) -> Unit
) : Controller(
        controllables,
        seeker,
        valueLabel,
        min, max, CoordinateSystemView.DEFAULT_CAMERA_DISTANCE
) {

    private val formatString = context.getString(R.string.transform_translation_value)

    override fun set(controllable: Controllable, value: Float) {
        setDistance(controllable, value)
    }

    override val valueLabelText: String
        get() = String.format(formatString, formatNumber(currentSeekerValue))

}
