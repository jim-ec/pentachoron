package io.jim.tesserapp.ui.model

import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
import io.jim.tesserapp.ui.view.ControllerView
import io.jim.tesserapp.ui.view.GraphicsView
import kotlinx.android.synthetic.main.view_controller.view.*

/**
 * Controller targeting the camera distance.
 */
fun cameraDistanceController(
        graphicsView: GraphicsView,
        view: ControllerView
) = run {

    val model = (view.context as MainActivity).viewModel

    Controller(
            seekBar = view.cameraDistanceSeekBar,
            watch = view.cameraDistanceWatch,
            valueRange = 3.0..15.0,
            startValue = model.cameraDistance.value,
            formatString = view.context.getString(R.string.camera_distance_value),
            onValueUpdate = { value ->
                graphicsView.synchronized { renderData ->
                    renderData.camera.distance = value
                }

                model.cameraDistance.value = value
            }
    )

}
