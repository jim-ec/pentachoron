package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.CoordinateSystemView

class CameraDistanceController(
        private val renderData: SharedRenderData,
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        min: Float = 3f,
        max: Float = 15f
) : Controller(
        ArrayList(), // TODO: temporary empty list
        seeker,
        valueLabel,
        min, max, CoordinateSystemView.DEFAULT_CAMERA_DISTANCE,
        context.getString(R.string.transform_translation_value)
) {

    override fun set(controllable: Controllable, value: Float) {
        TODO("Camera controller set called")
    }

    override fun setControlledValue(value: Float) {
        renderData.synchronized {
            renderData.camera.distance = currentSeekerValue
        }
    }

}
