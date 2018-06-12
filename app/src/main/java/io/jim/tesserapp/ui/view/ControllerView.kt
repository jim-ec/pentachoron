package io.jim.tesserapp.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
import io.jim.tesserapp.ui.model.*
import io.jim.tesserapp.util.LinearList
import kotlinx.android.synthetic.main.view_controller.view.*


/**
 * This view contains controls related to a graphics view or a controlled geometry.
 * But it does not host the graphics view instance itself.
 */
class ControllerView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val controllers = LinearList<Controller>()

    private val viewModelMonitor = (context as MainActivity).viewModel.monitor<MainViewModel>()

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Disable 4th dimensional seeker as that is a feature not implemented yet:
        qRotationSeekBar.isEnabled = false
        qTranslationSeekBar.isEnabled = false

        // Set theme-switch-button to currently set theme:
        darkThemeSwitch.isChecked = with(context as Activity) {
            getPreferences(Context.MODE_PRIVATE)
                    .getBoolean(getString(R.string.pref_dark_theme_enabled), false)
        }

        // Theme-switch-button triggers the activity to be recreated, with a new theme.
        darkThemeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (context as Activity).apply {

                setTheme(R.style.DarkTheme)

                // Remember selected theme choice in shared preferences.
                with(getPreferences(Context.MODE_PRIVATE).edit()) {
                    putBoolean(getString(R.string.pref_dark_theme_enabled), isChecked)
                    apply()
                }

                // Recreate instead of finish() and startActivity() so view-model persists:
                recreate()
            }
        }
    }

    /**
     * The graphics view the controllers target.
     */
    var targetGraphicsView: GraphicsView? = null
        set(graphicsView) {

            field = graphicsView

            // Remove previous controllers:
            controllers.indexedForEach {
                it.unlink()
            }
            controllers.clear()

            // If target is null, no controllers have to be allocated:
            graphicsView ?: return

            // Control render grid options:
            renderOptionGridSwitch.apply {

                // Set render grid option to current checked state:
                graphicsView.renderGrid = isChecked

                // Update the render grid option every times the checked state changes:
                setOnCheckedChangeListener { _, isChecked ->
                    graphicsView.renderGrid = isChecked
                }
            }

            // Link individual controllers to view-model entries:

            val viewModel = (context as MainActivity).viewModel

            // Camera distance:
            controllers += viewModel.cameraDistanceController(
                    context = context,
                    seekBar = cameraDistanceSeekBar,
                    watch = cameraDistanceWatch,
                    liveData = { cameraDistance }
            )

            // X rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = xRotationSeekBar,
                    watch = xRotationWatch,
                    liveData = { rotationX }
            )

            // Y rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = yRotationSeekBar,
                    watch = yRotationWatch,
                    liveData = { rotationY }
            )

            // Z rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = zRotationSeekBar,
                    watch = zRotationWatch,
                    liveData = { rotationZ }
            )

            // Q rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = qRotationSeekBar,
                    watch = qRotationWatch,
                    liveData = { rotationQ }
            )

            viewModelMonitor { ownedViewModel ->

                controllers += translationController(
                        context = context,
                        seekBar = xTranslationSeekBar,
                        watch = xTranslationWatch,
                        startValue = ownedViewModel.translationX.value,
                        viewModel = viewModel,
                        onTranslated = { viewModel, translation ->
                            viewModel.translationX.value = translation
                        }
                )

                controllers += translationController(
                        context = context,
                        seekBar = yTranslationSeekBar,
                        watch = yTranslationWatch,
                        startValue = ownedViewModel.translationY.value,
                        viewModel = viewModel,
                        onTranslated = { viewModel, translation ->
                            viewModel.translationY.value = translation
                        }
                )

                controllers += translationController(
                        context = context,
                        seekBar = zTranslationSeekBar,
                        watch = zTranslationWatch,
                        startValue = ownedViewModel.translationZ.value,
                        viewModel = viewModel,
                        onTranslated = { viewModel, translation ->
                            viewModel.translationZ.value = translation
                        }
                )

                controllers += translationController(
                        context = context,
                        seekBar = qTranslationSeekBar,
                        watch = qTranslationWatch,
                        startValue = ownedViewModel.translationQ.value,
                        viewModel = viewModel,
                        onTranslated = { viewModel, translation ->
                            viewModel.translationQ.value = translation
                        }
                )

            }

        }

}
