/*
 *  Created by Jim Eckerlein on 15.08.18 16:16
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 15.08.18 16:16
 */

package io.jim.tesserapp.ui

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.almeros.android.multitouch.MoveGestureDetector
import io.jim.tesserapp.R
import io.jim.tesserapp.graphics.Renderer
import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.math.ScrollAttractor
import io.jim.tesserapp.ui.main.MainViewModel
import io.jim.tesserapp.util.*
import kotlinx.coroutines.experimental.*

class GraphicsFragment : androidx.fragment.app.Fragment() {
    
    private lateinit var viewModel: MainViewModel
    
    /**
     * The timer, continuously forcing the graphics to redraw itself
     * and to call its pre draw listeners.
     */
    private var timerJob: Job? = null
    
    private val orbitScrollAttractor = ScrollAttractor(40L)
    private val orbitScrollAttractorDeltanizerX = FloatDeltanizer(0f)
    private val orbitScrollAttractorDeltanizerY = FloatDeltanizer(0f)
    
    private lateinit var orbitGestureDetector: MoveGestureDetector
    private lateinit var zoomGestureDetector: ScaleGestureDetector
    
    private lateinit var graphicsView: GLSurfaceView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Fetch view model:
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        
        orbitGestureDetector = MoveGestureDetector(activity,
                object : MoveGestureDetector.SimpleOnMoveGestureListener() {
                    override fun onMove(detector: MoveGestureDetector?) = consume {
                        detector ?: return NOT_CONSUMED
                        orbitScrollAttractor.scrollTo(detector.focusX, detector.focusY)
                    }
                })
        
        zoomGestureDetector = ScaleGestureDetector(activity,
                object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    override fun onScale(detector: ScaleGestureDetector?) = consume {
                        detector ?: return NOT_CONSUMED
                        
                        // A scale factor > 1 means that the pointer distance increased, i.e. zooming out:
                        viewModel.cameraDistance.value /= detector.scaleFactor
                        
                        // Constrain camera distance to a certain minimum:
                        viewModel.cameraDistance.value = Math.max(viewModel.cameraDistance.value, 2.0)
                    }
                })
        
        // Upon any transform data change, redraw the graphics view:
        with(viewModel) {
            listOf(
                    translationX, translationY, translationZ, translationQ,
                    rotationX, rotationY, rotationZ, rotationQ
            )
        }.forEach { transformLiveData ->
            transformLiveData.observe(this, Observer {
                graphicsView.invalidate()
            })
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        // Inform graphics view of fragment resume event to create graphics resources:
        graphicsView.onResume()
        
        // Launch timer:
        timerJob = launch {
            while (true) {
                graphicsView.postInvalidate()
                delay(16)
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        
        // Inform graphics view of fragment pause event to tear down graphics resources:
        graphicsView.onPause()
        
        // Cancel timer:
        timerJob?.cancel()
        timerJob = null
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        graphicsView = GLSurfaceView(activity).apply {
            setEGLContextClientVersion(2)
            setRenderer(Renderer(
                    Color(context.themedColorInt(R.attr.colorPrimary)),
                    viewModel,
                    context.assets,
                    resources.displayMetrics.xdpi.toDouble()))
            
            setOnTouchListener { _, event ->
                consume {
                    zoomGestureDetector.onTouchEvent(event)
                    orbitGestureDetector.onTouchEvent(event)
                }
            }
            
            viewTreeObserver.addOnPreDrawListener {
                consume {
                    synchronized(viewModel) {
                        viewModel.horizontalCameraRotation.value += orbitScrollAttractorDeltanizerX.delta / 300.0
                        viewModel.verticalCameraRotation.value -= orbitScrollAttractorDeltanizerY.delta / 300.0
                        
                        orbitScrollAttractor.computeNextApproximation().also {
                            orbitScrollAttractorDeltanizerX.new = it.x
                            orbitScrollAttractorDeltanizerY.new = it.y
                        }
                        
                    }
                }
            }
        }
        return graphicsView
    }
    
}
