/*
 *  Created by Jim Eckerlein on 7/23/18 9:35 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/23/18 9:35 AM
 */

package io.jim.tesserapp.ui.preferences

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.transaction

class PreferencesActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setTheme(preferenceThemeId())
        
        supportFragmentManager.transaction {
            replace(android.R.id.content, PreferencesFragment())
        }
    }
    
}
