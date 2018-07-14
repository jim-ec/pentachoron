package io.jim.tesserapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager
import io.jim.tesserapp.ui.model.MutableLiveDataNonNull

class PreferencesViewModel(application: Application) : AndroidViewModel(application) {
    
    val darkTheme = MutableLiveDataNonNull(
            PreferenceManager.getDefaultSharedPreferences(application).getBoolean(
                    application.getString(R.string.pref_dark_theme_key),
                    false))
    
    val grid = MutableLiveDataNonNull(
            PreferenceManager.getDefaultSharedPreferences(application).getBoolean(
                    application.getString(R.string.pref_grid_key),
                    false))
    
}
