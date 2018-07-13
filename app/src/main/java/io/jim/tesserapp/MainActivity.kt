package io.jim.tesserapp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.jim.tesserapp.geometry.SymbolicColorMapping
import io.jim.tesserapp.gl.Color
import io.jim.tesserapp.ui.model.MainViewModel
import io.jim.tesserapp.ui.view.themedColorInt
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
            symbolicColorMapping = SymbolicColorMapping(
                    primary = Color(themedColorInt(R.attr.colorPrimaryGeometry)),
                    accent = Color(themedColorInt(R.attr.colorAccent)),
                    x = Color(themedColorInt(R.attr.colorAxisX)),
                    y = Color(themedColorInt(R.attr.colorAxisY)),
                    z = Color(themedColorInt(R.attr.colorAxisZ)),
                    q = Color(themedColorInt(R.attr.colorAccent))
            )
        }
        
        // Set theme according to a shared preference.
        // Light theme is the default, and need not to be set explicitly therefore.
        if (getPreferences(Context.MODE_PRIVATE).getBoolean(
                        getString(R.string.pref_dark_theme_key), false)
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
