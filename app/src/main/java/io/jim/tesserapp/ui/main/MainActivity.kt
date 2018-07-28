/*
 *  Created by Jim Eckerlein on 7/28/18 10:42 AM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/28/18 10:42 AM
 */

package io.jim.tesserapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.Renderer
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.math.AttractionGestureListener
import io.jim.tesserapp.math.formatNumber
import io.jim.tesserapp.ui.preferences.PreferencesActivity
import io.jim.tesserapp.ui.preferences.gridPreference
import io.jim.tesserapp.ui.preferences.preferenceThemeId
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume
import io.jim.tesserapp.util.themedColorInt
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main activity.
 */
class MainActivity : AppCompatActivity() {
    
    lateinit var viewModel: MainViewModel
    
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
    
    override fun onOptionsItemSelected(item: MenuItem?) = consume {
        item ?: return NOT_CONSUMED
        when (item.itemId) {
            
            R.id.appbar_menu_preferences -> {
                // Open preference activity:
                startActivityForResult(
                        Intent(this, PreferencesActivity::class.java),
                        PREFERENCES_REQUEST)
            }
            
            R.id.appbar_menu_reset_transform -> {
                // Reset all transform:
                synchronized(viewModel) {
                    with(viewModel) {
                        listOf(rotationX, rotationY, rotationZ, rotationQ,
                                translationX, translationY, translationZ, translationQ)
                    }.forEach {
                        it.value = it.initialValue
                    }
                }
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
        
        listOf(axisButtonX to SelectedAxis.X,
                axisButtonY to SelectedAxis.Y,
                axisButtonZ to SelectedAxis.Z,
                axisButtonQ to SelectedAxis.Q).forEach { (button, axis) ->
            
            // When clicked, select the proper axis:
            button.setOnClickListener {
                synchronized(viewModel) {
                    viewModel.selectedAxis.value = axis
                }
            }
            
            // When selected axis changed, button selection state changes accordingly:
            viewModel.selectedAxis.observe(this, Observer { selectedAxis ->
                button.isSelected = axis == selectedAxis
            })
            
        }
        
        axisButtonX.isSelected = true
        
        swipeArea.apply {
    
            val transformToggle = GestureDetector(context,
                    object : GestureDetector.SimpleOnGestureListener() {
                        override fun onDoubleTap(e: MotionEvent?) = consume {
                            synchronized(viewModel) {
                                // Toggle between translate and rotate:
                                viewModel.transformMode.value =
                                        when (viewModel.transformMode.value) {
                                            TransformMode.ROTATE -> TransformMode.TRANSLATE
                                            TransformMode.TRANSLATE -> TransformMode.ROTATE
                                        }
                            }
                        }
                    })
    
            val swipeListener = MoveGestureDetector(context,
                    AttractionGestureListener(0.3f) { deltaApproximation ->
                        synchronized(viewModel) {
                            when (viewModel.transformMode.value) {
                        
                                TransformMode.ROTATE ->
                            
                                    when (viewModel.selectedAxis.value) {
                                        SelectedAxis.X -> viewModel.rotationX
                                        SelectedAxis.Y -> viewModel.rotationY
                                        SelectedAxis.Z -> viewModel.rotationZ
                                        SelectedAxis.Q -> viewModel.rotationQ
                                    }.value += 0.001 * deltaApproximation.x
                        
                                TransformMode.TRANSLATE ->
                            
                                    when (viewModel.selectedAxis.value) {
                                        SelectedAxis.X -> viewModel.translationX
                                        SelectedAxis.Y -> viewModel.translationY
                                        SelectedAxis.Z -> viewModel.translationZ
                                        SelectedAxis.Q -> viewModel.translationQ
                                    }.value += 0.02 * deltaApproximation.x
                            }
                        }
                    })
            
            viewModel.transformMode.observe(this@MainActivity, Observer<TransformMode> { mode ->
                text = getString(R.string.swipe_to_transform).format(when (mode!!) {
                    TransformMode.ROTATE -> getString(R.string.rotate)
                    TransformMode.TRANSLATE -> getString(R.string.translate)
                })
            })
            
            setOnTouchListener { _, motionEvent ->
                transformToggle.onTouchEvent(motionEvent)
                swipeListener.onTouchEvent(motionEvent)
            }
        }
        
        fun buildText(model: MainViewModel) = synchronized(model) {
            with(model) {
                "trans=(${formatNumber(translationX.value)}| " +
                        "${formatNumber(translationY.value)}| " +
                        "${formatNumber(translationZ.value)}| " +
                        "${formatNumber(translationQ.value)})\n" +
                        "rot=(${formatNumber(rotationX.value)}${getString(R.string.pi)}| " +
                        "${formatNumber(rotationY.value)}${getString(R.string.pi)}| " +
                        "${formatNumber(rotationZ.value)}${getString(R.string.pi)}| " +
                        "${formatNumber(rotationQ.value)}${getString(R.string.pi)})"
            }
        }
        viewModel.translationX.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        viewModel.translationY.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        viewModel.translationZ.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        viewModel.translationQ.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        viewModel.rotationX.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        viewModel.rotationY.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        viewModel.rotationZ.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        viewModel.rotationQ.observe(this, Observer { transformInfo.text = buildText(viewModel) })
        
        // Setup OpenGL surface view:
        glSurfaceView.apply {
            
            // Renderer uses OpenGL 2:
            setEGLContextClientVersion(2)
            setRenderer(Renderer(
                    Color(context.themedColorInt(android.R.attr.windowBackground)),
                    viewModel,
                    context.assets,
                    resources.displayMetrics.xdpi.toDouble()))
            
            val orbitDetector = MoveGestureDetector(context,
                    AttractionGestureListener(0.3f) { deltaApproximation ->
                        synchronized(viewModel) {
                            viewModel.horizontalCameraRotation.value += deltaApproximation.x / 300
                            viewModel.verticalCameraRotation.value -= deltaApproximation.y / 300
                        }
                    }
            )
            val zoomDetector = ScaleGestureDetector(context, ZoomGestureListener(viewModel))
            
            setOnTouchListener { _, event ->
                consume {
                    zoomDetector.onTouchEvent(event)
                    orbitDetector.onTouchEvent(event)
                }
            }
        }
    }
    
    /**
     * Pause render-thread.
     */
    override fun onStop() {
        super.onStop()
        glSurfaceView.onPause()
    }
    
    /**
     * Resume render-thread.
     */
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
    
}
