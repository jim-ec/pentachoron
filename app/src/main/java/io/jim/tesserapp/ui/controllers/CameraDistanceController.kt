package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.GraphicsView

class CameraDistanceController(
        private val renderData: SharedRenderData,
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        min: Double = 3.0,
        max: Double = 15.0
) : Controller(
        seeker,
        valueLabel,
        min, max, GraphicsView.DEFAULT_CAMERA_DISTANCE,
        context.getString(R.string.transform_translation_value)
) {

    override fun set(value: Double) {
        renderData.synchronized {
            renderData.camera.distance = currentSeekerValue
        }
    }

}
