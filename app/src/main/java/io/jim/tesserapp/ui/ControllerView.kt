package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.Switch
import io.jim.tesserapp.R
import java.util.*


/**
 * This view contains controls related to the coordinate system and its geometry.
 * But it does not host the coordinate system view instance itself.
 */
class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val controllables = ArrayList<Controllable>()
    private val controllers: List<Controller>

    /**
     * Add a [controllable] to the list of targets this controller controls.
     */
    operator fun plusAssign(controllable: Controllable) {
        controllables += controllable

        controllers.forEach {
            it.reevaluate()
        }
    }

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Pass render options:
        findViewById<Switch>(R.id.renderOptionGrid).setOnCheckedChangeListener { _, isChecked ->
            controllables.forEach {
                it.renderGrid = isChecked
            }
        }

        controllers = listOf(
                // X-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationX),
                        findViewById(R.id.valueRotationX)
                ) { controllable, rotation ->
                    controllable.rotationX = rotation
                },

                // Y-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationY),
                        findViewById(R.id.valueRotationY)
                ) { controllable, rotation ->
                    controllable.rotationY = rotation
                },

                // Z-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationZ),
                        findViewById(R.id.valueRotationZ)
                ) { controllable, rotation ->
                    controllable.rotationZ = rotation
                },

                // W-Rotation:
                RotationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerRotationW),
                        findViewById(R.id.valueRotationW)
                ) { controllable, rotation ->
                    controllable.rotationW = rotation
                },

                // X-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationX),
                        findViewById(R.id.valueTranslationX)
                ) { controllable, translation ->
                    controllable.translationX = translation
                },

                // Y-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationY),
                        findViewById(R.id.valueTranslationY)
                ) { controllable, translation ->
                    controllable.translationY = translation
                },

                // Z-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationZ),
                        findViewById(R.id.valueTranslationZ)
                ) { controllable, translation ->
                    controllable.translationZ = translation
                },

                // W-Translation:
                TranslationController(
                        controllables,
                        context,
                        findViewById(R.id.seekerTranslationW),
                        findViewById(R.id.valueTranslationW),
                        startValue = 1f
                ) { controllable, translation ->
                    controllable.translationW = translation
                },

                // Camera distance:
                object : Controller(
                        controllables,
                        findViewById(R.id.seekerCameraDistance),
                        findViewById(R.id.valueCameraDistance),
                        3f, 15f, CoordinateSystemView.DEFAULT_CAMERA_DISTANCE
                ) {
                    override fun set(controllable: Controllable, value: Float) {
                        controllable.cameraDistance = value
                    }

                    override val valueLabelText: String
                        get() = String.format(
                                context.getString(R.string.transform_translation_value),
                                currentSeekerValue
                        )
                }
        )
    }

}
