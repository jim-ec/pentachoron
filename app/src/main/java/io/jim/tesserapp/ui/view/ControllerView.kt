package io.jim.tesserapp.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
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
     * Instantiate controllers.
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

                // Recreate instead of finish() and startActivity() so view-model persists:
                recreate()
            }
        }


        // Camera distance:
        cameraDistanceController(this)

        (context as MainActivity).viewModel.apply {
            rotationController(context, xRotationSeekBar, xRotationWatch, rotationX.value, rotationX::setValue)
            rotationController(context, yRotationSeekBar, yRotationWatch, rotationY.value, rotationY::setValue)
            rotationController(context, zRotationSeekBar, zRotationWatch, rotationZ.value, rotationZ::setValue)
            rotationController(context, qRotationSeekBar, qRotationWatch, rotationQ.value, rotationQ::setValue)
            translationController(context, xTranslationSeekBar, xTranslationWatch, translationX.value, translationX::setValue)
            translationController(context, yTranslationSeekBar, yTranslationWatch, translationY.value, translationY::setValue)
            translationController(context, zTranslationSeekBar, zTranslationWatch, translationZ.value, translationZ::setValue)
            translationController(context, qTranslationSeekBar, qTranslationWatch, translationQ.value, translationQ::setValue)
        }

    }

}
