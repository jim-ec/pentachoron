/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.ui.preferences

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import io.jim.tesserapp.R
import io.jim.tesserapp.util.CONSUMED

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
