package io.jim.tesserapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction
import io.jim.tesserapp.util.preferenceThemeId

class PreferencesActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        setTheme(preferenceThemeId())
        
        supportFragmentManager.transaction {
            replace(android.R.id.content, PreferencesFragment())
        }
    }

}
