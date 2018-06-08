package io.jim.tesserapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.ui.model.MainViewModel


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
        viewModel.cameraDistance.observeNonNull(this) { cameraDistance ->
            println("Camera distance changed to: $cameraDistance")
        }

        if (getPreferences(Context.MODE_PRIVATE).getBoolean(
                        getString(R.string.pref_dark_theme_enabled), false)
        ) {
            setTheme(R.style.DarkTheme)
        }

        setContentView(R.layout.activity_main)
    }

}
