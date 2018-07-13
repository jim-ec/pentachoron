package io.jim.tesserapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction

class PreferencesActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        supportFragmentManager.transaction {
            replace(android.R.id.content, PreferencesFragment())
        }
    }

}
