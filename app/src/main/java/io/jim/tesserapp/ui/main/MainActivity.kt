/*
 *  Created by Jim Eckerlein on 7/20/18 10:37 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/20/18 10:37 PM
 */

package io.jim.tesserapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
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
    
    enum class TransformMode {
        ROTATE, TRANSLATE
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
        var transformMode = TransformMode.ROTATE
        
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
        
            val detector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            
                var previousEventTime = 0L
            
                override fun onDown(e: MotionEvent?) = CONSUMED
            
                override fun onScroll(
                        startEvent: MotionEvent?,
                        endEvent: MotionEvent?,
                        distanceX: Float,
                        distanceY: Float
                ) = consume {
                    endEvent ?: return NOT_CONSUMED
                    val timeDelta = endEvent.eventTime - previousEventTime
                    previousEventTime = endEvent.eventTime
                    if (timeDelta <= 0) return NOT_CONSUMED
                
                    if (transformMode == TransformMode.ROTATE) {
                    
                        when (selectedAxis) {
                            Axis.X -> viewModel.rotationX
                            Axis.Y -> viewModel.rotationY
                            Axis.Z -> viewModel.rotationZ
                            Axis.Q -> viewModel.rotationQ
                        }.value += 0.1 * (distanceX / (5 * timeDelta))
                    
                    } else if (transformMode == TransformMode.TRANSLATE) {
                    
                        when (selectedAxis) {
                            Axis.X -> viewModel.translationX
                            Axis.Y -> viewModel.translationY
                            Axis.Z -> viewModel.translationZ
                            Axis.Q -> viewModel.translationQ
                        }.value += 0.4 * (distanceX / timeDelta)
                    
                    }
                }
            
                override fun onSingleTapUp(e: MotionEvent?) = consume {
                    transformMode = when (transformMode) {
                        TransformMode.ROTATE -> TransformMode.TRANSLATE
                        TransformMode.TRANSLATE -> TransformMode.ROTATE
                    }
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
