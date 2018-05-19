package io.jim.tesserapp.ui.controllers

import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.ControllerView

/**
 * Controller targeting the camera distance.
 */
fun cameraDistanceController(
        renderData: SharedRenderData,
        view: ControllerView
) = Controller(
        seeker = view.findViewById(R.id.seekerCameraDistance),
        valueLabel = view.findViewById(R.id.valueCameraDistance),
        min = 3.0,
        max = 15.0,
        startValue = 8.0,
        formatString = view.context.getString(R.string.transform_translation_value),
        update = { value ->
            renderData.synchronized {
                renderData.camera.distance = value
            }
        }
)
