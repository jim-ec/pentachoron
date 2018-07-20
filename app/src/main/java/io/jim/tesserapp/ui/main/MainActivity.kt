/*
 *  Created by Jim Eckerlein on 7/20/18 9:50 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/20/18 9:50 PM
 */

package io.jim.tesserapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.ui.preferences.PreferencesActivity
import io.jim.tesserapp.util.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {
    
    lateinit var viewModel: MainViewModel
    
    enum class Axis {
        X, Y, Z, Q
    }
    
    companion object {
        
        /**
         * The preference activity is started with this identifying request code,
         * so that the callback [onActivityResult] can check whether is was called
         * because the user exited the preference activity.
         *
         * This will cause the activity to be recreated, as changing preferences affects
         * immutable state as the theme (which can be dark or light) or geometry like
         * the grid.
         */
        const val PREFERENCES_REQUEST = 1
    }
    
    override fun onCreateOptionsMenu(menu: Menu?) = consume {
        menuInflater.inflate(R.menu.appbar_menu, menu)
    }
    
    override fun onOptionsItemSelected(item: MenuItem) = consume {
        when (item.itemId) {
            R.id.appbar_menu_options -> {
                startActivityForResult(
                        Intent(this, PreferencesActivity::class.java),
                        PREFERENCES_REQUEST)
            }
        }
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PREFERENCES_REQUEST) {
            // Since preference changes affects immutable activity state like theme,
            // I just recreate the activity every times the user returned from
            // the preference screen:
            recreate()
        }
    }
    
    /**
     * Initialize activity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fetch view model:
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        
        // Set theme, which is only possible during this callback:
        setTheme(preferenceThemeId())
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar.apply { setTitle(R.string.tesseract) })
        
        // Generate geometry:
        viewModel.createGeometries(
                featuredGeometryName = getString(R.string.tesseract),
                enableGrid = gridPreference(),
                primaryColor = Color(themedColorInt(R.attr.colorPrimaryGeometry)),
                accentColor = Color(themedColorInt(R.attr.colorAccent)),
                xColor = Color(themedColorInt(R.attr.colorAxisX)),
                yColor = Color(themedColorInt(R.attr.colorAxisY)),
                zColor = Color(themedColorInt(R.attr.colorAxisZ))
        )
    
        var selectedAxis = Axis.X
        
        val axisButtonList = listOf(axisButtonX, axisButtonY, axisButtonZ, axisButtonQ)
        axisButtonList.forEach { axisButton ->
            axisButton.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    // Uncheck all the other buttons:
                    axisButtonList.forEach {
                        it.isChecked = it.id == axisButton.id
                    }
                    // Remember selected axis:
                    selectedAxis = when (axisButton.id) {
                        R.id.axisButtonX -> Axis.X
                        R.id.axisButtonY -> Axis.Y
                        R.id.axisButtonZ -> Axis.Z
                        R.id.axisButtonQ -> Axis.Q
                        else -> throw Exception("Unknown axis button")
                    }
                } else {
                    // Prevent the case that no button is checked by
                    // checking back this button:
                    if (axisButtonList.none { it.isChecked }) {
                        axisButton.isChecked = true
                    }
                }
            }
        }
    
        pushArea.apply {
            val detector = MoveGestureDetector(context,
                    object : MoveGestureDetector.SimpleOnMoveGestureListener() {
                        override fun onMove(detector: MoveGestureDetector?) = consume {
                            detector ?: return NOT_CONSUMED
                            if (detector.timeDelta == 0L) return NOT_CONSUMED
                        
                            val target = when (selectedAxis) {
                                MainActivity.Axis.X -> viewModel.rotationX
                                MainActivity.Axis.Y -> viewModel.rotationY
                                MainActivity.Axis.Z -> viewModel.rotationZ
                                MainActivity.Axis.Q -> viewModel.rotationQ
                            }
                        
                            // Used sensitivity for translation:
                            //target.value += 0.4 * (detector.focusDelta.x / detector.timeDelta)
                        
                            target.value += 0.1 * (detector.focusDelta.x / (5 * detector.timeDelta))
                        }
                    })
        
            setOnTouchListener { _, motionEvent ->
                detector.onTouchEvent(motionEvent)
            }
        }
    }
    
    /**
     * Pause render-thread.
     */
    override fun onStop() {
        super.onStop()
        graphicsView.onPause()
    }
    
    /**
     * Resume render-thread.
     */
    override fun onResume() {
        super.onResume()
        graphicsView.onResume()
    }
    
}
