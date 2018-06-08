package io.jim.tesserapp

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.jim.tesserapp.ui.model.Model


/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {

    lateinit var model: Model

    /**
     * Initialize activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Fetch view model:
        model = ViewModelProviders.of(this).get(Model::class.java)
        model.cameraDistance.observe(this) { cameraDistance ->
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
