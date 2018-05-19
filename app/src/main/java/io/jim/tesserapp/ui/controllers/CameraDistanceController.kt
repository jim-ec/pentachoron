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
        valueLabel: TextView
) : Controller(
        seeker = seeker,
        valueLabel = valueLabel,
        min = 3.0,
        max = 15.0,
        startValue = GraphicsView.DEFAULT_CAMERA_DISTANCE,
        formatString = context.getString(R.string.transform_translation_value)
) {

    override fun update(value: Double) {
        renderData.synchronized {
            renderData.camera.distance = value
        }
    }

}
