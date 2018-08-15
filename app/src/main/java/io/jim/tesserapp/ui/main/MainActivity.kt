/*
 *  Created by Jim Eckerlein on 8/5/18 10:51 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 8/5/18 10:49 AM
 */

package io.jim.tesserapp.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.ui.preferences.gridPreference
import io.jim.tesserapp.ui.preferences.preferenceThemeId
import io.jim.tesserapp.util.themedColorInt

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set theme, which is only possible during this callback:
        setTheme(preferenceThemeId())
        setContentView(R.layout.activity_main)
        
        // Generate geometry:
        ViewModelProviders.of(this).get(MainViewModel::class.java).createGeometries(
                featuredGeometryName = getString(R.string.tesseract),
                enableGrid = gridPreference(),
                primaryColor = Color(themedColorInt(R.attr.colorPrimaryGeometry)),
                accentColor = Color(themedColorInt(R.attr.colorAccent)),
                xColor = Color(themedColorInt(R.attr.colorAxisX)),
                yColor = Color(themedColorInt(R.attr.colorAxisY)),
                zColor = Color(themedColorInt(R.attr.colorAxisZ))
        )
        
        
    }
    
}
