package io.jim.tesserapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.ui.model.MainViewModel
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

        // Set theme according to a shared preference.
        // Light theme is the default, and need not to be set explicitly therefore.
        if (getPreferences(Context.MODE_PRIVATE).getBoolean(
                        getString(R.string.pref_dark_theme_enabled), false)
        ) {
            setTheme(R.style.DarkTheme)
        }

        setContentView(R.layout.activity_main)

        // Associate the controller with the graphics view to control:
        controllerView.targetGraphicsView = cubeView.graphicsView
    }

    /**
     * Pause render-thread.
     */
    override fun onStop() {
        super.onStop()
        cubeView.graphicsView.onPause()
    }

    /**
     * Resume render-thread.
     */
    override fun onResume() {
        super.onResume()
        cubeView.graphicsView.onResume()
    }

}
