package io.jim.tesserapp.ui.model

import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
import io.jim.tesserapp.ui.view.ControllerView
import kotlinx.android.synthetic.main.view_controller.view.*

/**
 * Controller targeting the camera distance.
 */
fun cameraDistanceController(view: ControllerView) =
        run {
            val model = (view.context as MainActivity).viewModel

            Controller(
                    seekBar = view.cameraDistanceSeekBar,
                    watch = view.cameraDistanceWatch,
                    valueRange = 3.0..15.0,
                    startValue = model.cameraDistance.value,
                    formatString = view.context.getString(R.string.camera_distance_watch_format),
                    onValueUpdate = { value ->
                        model.cameraDistance.value = value
                    }
            )

        }
