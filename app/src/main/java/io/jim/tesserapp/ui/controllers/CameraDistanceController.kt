package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData

/**
 * Controller targeting the camera distance.
 */
fun cameraDistanceController(
        renderData: SharedRenderData,
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView
) = Controller(
        seeker = seeker,
        valueLabel = valueLabel,
        min = 3.0,
        max = 15.0,
        startValue = 8.0,
        formatString = context.getString(R.string.transform_translation_value),
        update = { value ->
            renderData.synchronized {
                renderData.camera.distance = value
            }
        }
)
