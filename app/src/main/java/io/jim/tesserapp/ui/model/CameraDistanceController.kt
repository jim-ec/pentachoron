package io.jim.tesserapp.ui.model

import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.view.ControllerView
import kotlinx.android.synthetic.main.view_controller.view.*

/**
 * Controller targeting the camera distance.
 */
fun cameraDistanceController(
        renderData: SharedRenderData,
        view: ControllerView
) = Controller(
        seekBar = view.cameraDistanceSeekBar,
        watch = view.cameraDistanceWatch,
        valueRange = 3.0..15.0,
        startValue = 8.0,
        formatString = view.context.getString(R.string.camera_distance_value),
        onValueUpdate = { value ->
            renderData.synchronized {
                renderData.camera.distance = value
            }
        }
)
