package io.jim.tesserapp

import android.os.Bundle

class PreferencesFragment : androidx.preference.PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
    
}
