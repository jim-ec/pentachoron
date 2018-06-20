package io.jim.tesserapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.themedColorInt
import io.jim.tesserapp.ui.model.MainViewModel
import io.jim.tesserapp.util.synchronized
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {
    
    lateinit var viewModel: MainViewModel
    
    /**
     * Initialize activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fetch view model:
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    
        viewModel.synchronized {
            colorResolver = { color ->
                when (color) {
                    Geometry.Color.PRIMARY -> themedColorInt(R.attr.colorPrimaryGeometry)
                    Geometry.Color.ACCENT -> themedColorInt(R.attr.colorAccent)
                    Geometry.Color.X -> themedColorInt(R.attr.colorAxisX)
                    Geometry.Color.Y -> themedColorInt(R.attr.colorAxisY)
                    Geometry.Color.Z -> themedColorInt(R.attr.colorAxisZ)
                    Geometry.Color.Q -> themedColorInt(R.attr.colorAccent)
                }
            }
        }
        
        // Set theme according to a shared preference.
        // Light theme is the default, and need not to be set explicitly therefore.
        if (getPreferences(Context.MODE_PRIVATE).getBoolean(
                        getString(R.string.pref_dark_theme_enabled), false)
        ) {
            setTheme(R.style.DarkTheme)
        }
        
        setContentView(R.layout.activity_main)
        
        // Associate the controller with the graphics view to control:
        controllerView.targetGraphicsView = graphicsView
    }
    
    /**
     * Pause render-thread.
     */
    override fun onStop() {
        super.onStop()
        graphicsView.onPause()
    }
    
    /**
     * Resume render-thread.
     */
    override fun onResume() {
        super.onResume()
        graphicsView.onResume()
    }
    
}
