/*
 *  Created by Jim Eckerlein on 15.08.18 19:41
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 15.08.18 19:41
 */

package io.jim.tesserapp.ui

import android.os.Bundle
import android.view.*
import android.widget.Scroller
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import io.jim.tesserapp.R
import io.jim.tesserapp.math.ScrollAttractor
import io.jim.tesserapp.ui.main.MainViewModel
import io.jim.tesserapp.util.CONSUMED
import io.jim.tesserapp.util.FloatDeltanizer
import io.jim.tesserapp.util.NOT_CONSUMED
import io.jim.tesserapp.util.consume
import kotlinx.android.synthetic.main.fragment_controller.*

class ControllerFragment : Fragment() {
    
    lateinit var viewModel: MainViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }
    
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_controller, container, false)
    }
    
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        
        val transformFlingScroller = Scroller(activity)
        val transformFlingScrollerDeltanizer = FloatDeltanizer(0f)
        
        val transformScrollAttractor = ScrollAttractor(50L)
        val transformScrollAttractorDeltanizer = FloatDeltanizer(0f)
        
        listOf(
                axisButtonX to MainViewModel.SelectedAxis.X,
                axisButtonY to MainViewModel.SelectedAxis.Y,
                axisButtonZ to MainViewModel.SelectedAxis.Z,
                axisButtonQ to MainViewModel.SelectedAxis.Q
        ).forEach { (button, axis) ->
            
            // When clicked, select the proper axis:
            button.setOnClickListener {
                synchronized(viewModel) {
                    viewModel.selectedAxis.value = axis
                }
                
                // Immediately reset any scroll effect:
                transformFlingScroller.forceFinished(true)
                transformScrollAttractor.haltApproximation()
            }
            
            // When selected axis changed, button selection state changes accordingly:
            viewModel.selectedAxis.observe(this, Observer { selectedAxis ->
                button.isSelected = axis == selectedAxis
            })
            
        }
        
        val transformScrollGestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onDown(event: MotionEvent?) = consume {
                        event ?: return NOT_CONSUMED
                        transformScrollAttractor.resetScrollTo(event.x, event.y)
                        transformScrollAttractorDeltanizer.reset(event.x)
                        transformFlingScroller.forceFinished(true)
                    }
                    
                    override fun onScroll(
                            startEvent: MotionEvent?,
                            currentEvent: MotionEvent?,
                            distanceX: Float,
                            distanceY: Float
                    ) = consume {
                        currentEvent ?: return NOT_CONSUMED
                        transformScrollAttractor.scrollTo(currentEvent.x, currentEvent.y)
                    }
                })
        
        val transformFlingGestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onFling(
                            startEvent: MotionEvent?,
                            endEvent: MotionEvent?,
                            velocityX: Float,
                            velocityY: Float
                    ) = consume {
                        startEvent ?: return NOT_CONSUMED
                        transformFlingScrollerDeltanizer.reset(0f)
                        transformFlingScroller.fling(
                                0,
                                0,
                                velocityX.toInt() / 4,
                                velocityY.toInt() / 4,
                                Int.MIN_VALUE,
                                Int.MAX_VALUE,
                                0, 0)
                    }
                })
        
        // Toggle between translation and rotation mode:
        val toggleTransformGestureDetector = GestureDetector(context,
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onLongPress(e: MotionEvent?) {
                        transformFlingScroller.forceFinished(true)
                        synchronized(viewModel) {
                            viewModel.transformMode.value =
                                    when (viewModel.transformMode.value) {
                                        MainViewModel.TransformMode.ROTATE -> MainViewModel.TransformMode.TRANSLATE
                                        MainViewModel.TransformMode.TRANSLATE -> MainViewModel.TransformMode.ROTATE
                                    }
                        }
                    }
                })
        
        swipeArea.apply {
            
            viewTreeObserver.addOnPreDrawListener {
                
                transformFlingScroller.computeScrollOffset()
                val currentScrollApproximation = transformScrollAttractor.computeNextApproximation()
                
                synchronized(viewModel) {
                    
                    when (viewModel.transformMode.value) {
                        
                        MainViewModel.TransformMode.ROTATE ->
                            
                            when (viewModel.selectedAxis.value) {
                                MainViewModel.SelectedAxis.X -> viewModel.rotationX
                                MainViewModel.SelectedAxis.Y -> viewModel.rotationY
                                MainViewModel.SelectedAxis.Z -> viewModel.rotationZ
                                MainViewModel.SelectedAxis.Q -> viewModel.rotationQ
                            }.value += 0.001 * (transformFlingScrollerDeltanizer.delta +
                                    transformScrollAttractorDeltanizer.delta)
                        
                        MainViewModel.TransformMode.TRANSLATE ->
                            
                            when (viewModel.selectedAxis.value) {
                                MainViewModel.SelectedAxis.X -> viewModel.translationX
                                MainViewModel.SelectedAxis.Y -> viewModel.translationY
                                MainViewModel.SelectedAxis.Z -> viewModel.translationZ
                                MainViewModel.SelectedAxis.Q -> viewModel.translationQ
                            }.value += 0.02 * (transformFlingScrollerDeltanizer.delta +
                                    transformScrollAttractorDeltanizer.delta)
                    }
                }
                
                transformFlingScrollerDeltanizer.new = transformFlingScroller.currX.toFloat()
                transformScrollAttractorDeltanizer.new = currentScrollApproximation.x
                
                CONSUMED
            }
            
            viewModel.transformMode.observe(this@ControllerFragment, Observer<MainViewModel.TransformMode> { mode ->
                text = getString(R.string.swipe_to_transform).format(when (mode!!) {
                    MainViewModel.TransformMode.ROTATE -> getString(R.string.rotate)
                    MainViewModel.TransformMode.TRANSLATE -> getString(R.string.translate)
                })
            })
            
            setOnTouchListener { _, event ->
                consume {
                    transformScrollGestureDetector.onTouchEvent(event)
                    toggleTransformGestureDetector.onTouchEvent(event)
                    transformFlingGestureDetector.onTouchEvent(event)
                }
            }
            
        }
    }
    
}
