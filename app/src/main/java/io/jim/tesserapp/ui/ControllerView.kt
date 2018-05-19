package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Switch
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.controllers.*


/**
 * This view contains controls related to the coordinate system and its geometry.
 * But it does not host the coordinate system view instance itself.
 */
class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private lateinit var controlTarget: Controllable
    private lateinit var controllers: List<Controller>

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Pass render options:
        findViewById<Switch>(R.id.renderOptionGrid).setOnCheckedChangeListener { _, isChecked ->
            controlTarget.renderGrid = isChecked
        }

        // Disable 4th dimensional seeker as that is a feature not implemented yet:
        findViewById<SeekBar>(R.id.seekerRotationQ).isEnabled = false
        findViewById<SeekBar>(R.id.seekerTranslationQ).isEnabled = false
    }

    /**
     * Declare [controllable] to be controlled by this controller.
     */
    fun control(renderData: SharedRenderData, controllable: Controllable) {
        controlTarget = controllable

        controllers = listOf(

                // X-Rotation:
                RotationController(
                        context,
                        findViewById(R.id.seekerRotationX),
                        findViewById(R.id.valueRotationX)
                ) { rotation ->
                    renderData.controlledGeometry.smoothRotation.x = rotation
                },

                // Y-Rotation:
                RotationController(
                        context,
                        findViewById(R.id.seekerRotationY),
                        findViewById(R.id.valueRotationY)
                ) { rotation ->
                    controlTarget.rotation.y = rotation
                },

                // Z-Rotation:
                RotationController(
                        context,
                        findViewById(R.id.seekerRotationZ),
                        findViewById(R.id.valueRotationZ)
                ) { rotation ->
                    controlTarget.rotation.z = rotation
                },

                // Q-Rotation:
                RotationController(
                        context,
                        findViewById(R.id.seekerRotationQ),
                        findViewById(R.id.valueRotationQ)
                ) { rotation ->
                    controlTarget.rotation.q = rotation
                },

                // X-Translation:
                TranslationController(
                        context,
                        findViewById(R.id.seekerTranslationX),
                        findViewById(R.id.valueTranslationX)
                ) { translation ->
                    controlTarget.translation.x = translation
                },

                // Y-Translation:
                TranslationController(
                        context,
                        findViewById(R.id.seekerTranslationY),
                        findViewById(R.id.valueTranslationY)
                ) { translation ->
                    controlTarget.translation.y = translation
                },

                // Z-Translation:
                TranslationController(
                        context,
                        findViewById(R.id.seekerTranslationZ),
                        findViewById(R.id.valueTranslationZ)
                ) { translation ->
                    controlTarget.translation.z = translation
                },

                // W-Translation:
                TranslationController(
                        context,
                        findViewById(R.id.seekerTranslationQ),
                        findViewById(R.id.valueTranslationQ)
                ) { translation ->
                    controlTarget.translation.q = translation
                },

                CameraDistanceController(
                        renderData,
                        context,
                        findViewById(R.id.seekerCameraDistance),
                        findViewById(R.id.valueCameraDistance)
                )
        )

    }

}
