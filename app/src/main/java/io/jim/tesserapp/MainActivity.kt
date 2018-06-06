package io.jim.tesserapp

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity


/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {

    /**
     * Initialize activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (getPreferences(Context.MODE_PRIVATE).getBoolean(
                        getString(R.string.pref_dark_theme_enabled), false)
        ) {
            setTheme(R.style.DarkTheme)
        }

        setContentView(R.layout.activity_main)
    }

}
