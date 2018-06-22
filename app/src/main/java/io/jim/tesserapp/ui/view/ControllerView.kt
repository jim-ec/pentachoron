package io.jim.tesserapp.ui.view

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.AdapterView
import android.widget.FrameLayout
import io.jim.tesserapp.MainActivity
import io.jim.tesserapp.R
import io.jim.tesserapp.ui.model.*
import io.jim.tesserapp.util.synchronized
import kotlinx.android.synthetic.main.view_controller.view.*


/**
 * This view contains controls related to a graphics view or a controlled geometry.
 * But it does not host the graphics view instance itself.
 */
class ControllerView : FrameLayout {
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private val controllers = ArrayList<Controller>()
    
    init {
        View.inflate(context, R.layout.view_controller, this)
        
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
            
            val viewModel = (context as MainActivity).viewModel
            
            // Remove previous controllers:
            controllers.forEach {
                it.unlink()
            }
            controllers.clear()
            
            // If target is null, no controllers have to be allocated:
            graphicsView ?: return
            
            // Control render grid options:
            renderOptionGridSwitch.apply {
                
                // Set render grid option to current checked state:
                viewModel.synchronized {
                    enableGrid = isChecked
                }
                
                // Update the render grid option every times the checked state changes:
                setOnCheckedChangeListener { _, isChecked ->
                    viewModel.synchronized {
                        enableGrid = isChecked
                    }
                }
            }
    
            renderModeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    viewModel.synchronized {
                        viewModel.renderMode = when (position) {
                            0 -> MainViewModel.RenderMode.WIREFRAME_PROJECTION
                            1 -> MainViewModel.RenderMode.COLLAPSE_Z
                            else -> throw RuntimeException("Unknown render mode $position selected")
                        }
                    }
                }
        
                override fun onNothingSelected(parent: AdapterView<*>?) {}
        
            }
            
            // Link individual controllers to view-model entries:
            
            // Camera distance:
            controllers += viewModel.cameraDistanceController(
                    context = context,
                    seekBar = cameraDistanceSeekBar,
                    watch = cameraDistanceWatch
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
            
            // X translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = xTranslationSeekBar,
                    watch = xTranslationWatch,
                    liveData = { translationX }
            )
            
            // Y translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = yTranslationSeekBar,
                    watch = yTranslationWatch,
                    liveData = { translationY }
            )
            
            // Z translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = zTranslationSeekBar,
                    watch = zTranslationWatch,
                    liveData = { translationZ }
            )
            
            // Q translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = qTranslationSeekBar,
                    watch = qTranslationWatch,
                    liveData = { translationQ }
            )
            
        }
    
}
