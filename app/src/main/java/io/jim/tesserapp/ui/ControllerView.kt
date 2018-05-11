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
import java.util.*


/**
 * This view contains controls related to the coordinate system and its geometry.
 * But it does not host the coordinate system view instance itself.
 */
class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val controllables = ArrayList<Controllable>()
    private val controllers: MutableList<Controller>

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Pass render options:
        findViewById<Switch>(R.id.renderOptionGrid).setOnCheckedChangeListener { _, isChecked ->
            controllables.forEach {
                it.renderGrid = isChecked
            }
        }

        // Disable 4th dimensional seeker as that is a feature not implemented yet:
        findViewById<SeekBar>(R.id.seekerRotationQ).isEnabled = false
        findViewById<SeekBar>(R.id.seekerTranslationQ).isEnabled = false

        controllers = mutableListOf(
                // X-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationX),
                        findViewById(R.id.valueRotationX)
                ) { controllable, rotation ->
                    controllable.rotation.x = rotation
                },

                // Y-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationY),
                        findViewById(R.id.valueRotationY)
                ) { controllable, rotation ->
                    controllable.rotation.y = rotation
                },

                // Z-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationZ),
                        findViewById(R.id.valueRotationZ)
                ) { controllable, rotation ->
                    controllable.rotation.z = rotation
                },

                // Q-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationQ),
                        findViewById(R.id.valueRotationQ)
                ) { controllable, rotation ->
                    controllable.rotation.q = rotation
                },

                // X-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationX),
                        findViewById(R.id.valueTranslationX)
                ) { controllable, translation ->
                    controllable.translation.x = translation
                },

                // Y-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationY),
                        findViewById(R.id.valueTranslationY)
                ) { controllable, translation ->
                    controllable.translation.y = translation
                },

                // Z-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationZ),
                        findViewById(R.id.valueTranslationZ)
                ) { controllable, translation ->
                    controllable.translation.z = translation
                },

                // W-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationQ),
                        findViewById(R.id.valueTranslationQ)
                ) { controllable, translation ->
                    controllable.translation.q = translation
                }
        )
    }

    /**
     * Add a [controllable] to the list of targets this controller controls.
     */
    operator fun plusAssign(controllable: Controllable) {
        controllables += controllable

        controllable.setup(this)

        controllers.forEach {
            it.reevaluate()
        }
    }

    fun controlCamera(renderData: SharedRenderData) {
        val controller = CameraDistanceController(
                renderData,
                context,
                findViewById(R.id.seekerCameraDistance),
                findViewById(R.id.valueCameraDistance)
        )

        controller.reevaluate()

        controllers += controller
    }

}
