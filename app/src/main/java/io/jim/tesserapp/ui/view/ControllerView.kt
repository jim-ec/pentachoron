package io.jim.tesserapp.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.model.cameraDistanceController
import io.jim.tesserapp.ui.model.rotationController
import io.jim.tesserapp.ui.model.translationController
import kotlinx.android.synthetic.main.view_controller.view.*


/**
 * This view contains controls related to a graphics view or a controlled geometry.
 * But it does not host the graphics view instance itself.
 *
 * Before the seek bar can actually send data, you must call [control] **once**.
 * By wrapping the [ControllerView] and [GraphicsView] into a [ControlledGraphicsContainerView],
 * this is done automatically.
 */
class ControllerView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Disable 4th dimensional seeker as that is a feature not implemented yet:
        qRotationSeekBar.isEnabled = false
        qTranslationSeekBar.isEnabled = false
    }

    /**
     * Instantiate controllers piping values from seek bars to [graphicsView] and
     * the [SharedRenderData.featuredGeometry].
     */
    fun control(graphicsView: GraphicsView) {

        // Control render grid options:
        renderOptionGridSwitch.also {
            // Set render grid option to current checked state:
            graphicsView.renderGrid = it.isChecked
        }.setOnCheckedChangeListener { _, isChecked ->
            // Update the render grid option every times the checked state changes:
            graphicsView.renderGrid = isChecked
        }

        darkThemeSwitch.isChecked = with(context as Activity) {
            getPreferences(Context.MODE_PRIVATE).getBoolean(getString(R.string.pref_dark_theme_enabled), false)
        }

        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (context as Activity).apply {

                setTheme(R.style.DarkTheme)

                with(getPreferences(Context.MODE_PRIVATE).edit()) {
                    putBoolean(getString(R.string.pref_dark_theme_enabled), isChecked)
                    apply()
                }

                finish()
                startActivity(intent)
            }
        }

        graphicsView.sharedRenderData.featuredGeometry.transform.also {

            // Camera distance:
            cameraDistanceController(graphicsView.sharedRenderData, this)

            // X-Rotation:
            var oldXRotation = 0.0

            rotationController(context, xRotationSeekBar, xRotationWatch) { rotation ->
                it.rotateX(rotation - oldXRotation)
                oldXRotation = rotation
            }

            // Y-Rotation:
            var oldYRotation = 0.0

            rotationController(context, yRotationSeekBar, yRotationWatch) { rotation ->
                it.rotateY(rotation - oldYRotation)
                oldYRotation = rotation
            }

            // Z-Rotation:
            var oldZRotation = 0.0

            rotationController(context, zRotationSeekBar, zRotationWatch) { rotation ->
                it.rotateZ(rotation - oldZRotation)
                oldZRotation = rotation
            }

            // Q-Rotation:
            rotationController(context, qRotationSeekBar, qRotationWatch) {}

            // X-Translation:
            var oldXTranslation = 0.0

            translationController(context, xTranslationSeekBar, xTranslationWatch
            ) { translation ->
                it.translateX(translation - oldXTranslation)
                oldXTranslation = translation
            }

            // Y-Translation:
            var oldYTranslation = 0.0

            translationController(context, yTranslationSeekBar, yTranslationWatch) { translation ->
                it.translateY(translation - oldYTranslation)
                oldYTranslation = translation
            }

            // Z-Translation:
            var oldZTranslation = 0.0

            translationController(context, zTranslationSeekBar, zTranslationWatch) { translation ->
                it.translateZ(translation - oldZTranslation)
                oldZTranslation = translation
            }

            // Q-Translation:
            translationController(context, qTranslationSeekBar, qTranslationWatch) { }

        }

    }

}
