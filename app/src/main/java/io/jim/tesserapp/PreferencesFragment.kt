package io.jim.tesserapp

import android.os.Bundle
import android.view.MenuItem
import androidx.preference.CheckBoxPreference

class PreferencesFragment : androidx.preference.PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    
        (findPreference(getString(R.string.pref_dark_theme_key)) as CheckBoxPreference)
                .setOnPreferenceChangeListener { _, _ ->
                    // Recreate activity in order to show new theme:
                    activity!!.recreate()
                    CONSUMED
                }
        
    }
    
}
