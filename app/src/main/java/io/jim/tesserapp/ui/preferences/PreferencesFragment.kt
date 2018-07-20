/*
 *  Created by Jim Eckerlein on 7/16/18 1:56 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/16/18 1:56 PM
 */

package io.jim.tesserapp.ui.preferences

import android.os.Bundle
import androidx.preference.ListPreference
import io.jim.tesserapp.R
import io.jim.tesserapp.util.consume

class PreferencesFragment : androidx.preference.PreferenceFragmentCompat() {
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    
        (findPreference(getString(R.string.pref_theme_key)) as ListPreference)
                .setOnPreferenceChangeListener { _, _ ->
                    consume {
                        // Recreate activity in order to show new theme:
                        activity!!.recreate()
                    }
                }
        
    }
    
}
