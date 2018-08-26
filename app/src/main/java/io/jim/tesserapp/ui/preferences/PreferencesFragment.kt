/*
 *  Created by Jim Eckerlein on 7/16/18 1:56 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/16/18 1:56 PM
 */

package io.jim.tesserapp.ui.preferences

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import io.jim.tesserapp.R
import io.jim.tesserapp.util.consume

class PreferencesFragment : androidx.preference.PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        listOf(
                findPreference(getString(R.string.pref_theme_key)),
                findPreference(getString(R.string.pref_grid_key))
        ).forEach {
            it.setOnPreferenceChangeListener { _, _ ->
                consume {
                    activity!!.recreate()
                }
            }
        }
        
    }
    
}
