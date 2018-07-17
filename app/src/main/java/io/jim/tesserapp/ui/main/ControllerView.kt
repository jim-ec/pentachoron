/*
 *  Created by Jim Eckerlein on 7/17/18 5:20 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/17/18 5:18 PM
 */

package io.jim.tesserapp.ui.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import io.jim.tesserapp.R
import kotlinx.android.synthetic.main.view_controller.view.*


/**
 * This view contains controls related to a graphics view or a controlled geometry.
 * But it does not host the graphics view instance itself.
 */
class ControllerView : FrameLayout {
    
    constructor(context: Context) : super(context)
    
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    
    private val controllers = ArrayList<Controller>()
    
    init {
        View.inflate(context, R.layout.view_controller, this)
    }
    
    /**
     * The graphics view the controllers target.
     */
    var targetGraphicsView: GraphicsView? = null
        set(graphicsView) {
            
            field = graphicsView
            
            val viewModel = (context as MainActivity).viewModel
            
            // Remove previous controllers:
            controllers.forEach {
                it.unlink()
            }
            controllers.clear()
            
            // If target is null, no controllers have to be allocated:
            graphicsView ?: return
            
            // Link individual controllers to view-model entries:
            
            // X rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = xRotationSeekBar,
                    watch = xRotationWatch,
                    liveData = { rotationX }
            )
            
            // Y rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = yRotationSeekBar,
                    watch = yRotationWatch,
                    liveData = { rotationY }
            )
            
            // Z rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = zRotationSeekBar,
                    watch = zRotationWatch,
                    liveData = { rotationZ }
            )
            
            // Q rotation:
            controllers += viewModel.rotationController(
                    context = context,
                    seekBar = qRotationSeekBar,
                    watch = qRotationWatch,
                    liveData = { rotationQ }
            )
            
            // X translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = xTranslationSeekBar,
                    watch = xTranslationWatch,
                    liveData = { translationX }
            )
            
            // Y translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = yTranslationSeekBar,
                    watch = yTranslationWatch,
                    liveData = { translationY }
            )
            
            // Z translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = zTranslationSeekBar,
                    watch = zTranslationWatch,
                    liveData = { translationZ }
            )
            
            // Q translation:
            controllers += viewModel.translationController(
                    context = context,
                    seekBar = qTranslationSeekBar,
                    watch = qTranslationWatch,
                    liveData = { translationQ }
            )
            
        }
    
}
