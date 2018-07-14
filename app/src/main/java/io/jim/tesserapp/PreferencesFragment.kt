package io.jim.tesserapp

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.ViewModelProviders
import androidx.preference.CheckBoxPreference

class PreferencesFragment : androidx.preference.PreferenceFragmentCompat() {
    
    lateinit var viewModel: PreferencesViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        viewModel = ViewModelProviders.of(this).get(PreferencesViewModel::class.java)
    }
    
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    
        (findPreference(getString(R.string.pref_dark_theme_key)) as CheckBoxPreference)
                .setOnPreferenceChangeListener { _, isSet ->
                    viewModel.darkTheme.value = isSet as? Boolean ?: throw Exception()
                    CONSUMED
                }
    
        (findPreference(getString(R.string.pref_grid_key)) as CheckBoxPreference)
                .setOnPreferenceChangeListener { _, isSet ->
                    viewModel.grid.value = isSet as? Boolean ?: throw Exception()
                    CONSUMED
                }
        
    }
    
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        println("Preference selected: ${item?.title}")
        return super.onOptionsItemSelected(item)
    }
    
}
