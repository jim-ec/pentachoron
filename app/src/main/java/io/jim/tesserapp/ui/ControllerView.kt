package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Switch
import io.jim.tesserapp.R
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.graphics.SharedRenderData
import io.jim.tesserapp.ui.controllers.cameraDistanceController
import io.jim.tesserapp.ui.controllers.rotationController
import io.jim.tesserapp.ui.controllers.translationController


/**
 * This view contains controls related to a graphics view or a controlled geometry.
 * But it does not host the graphics view instance itself.
 *
 * Before the seek bar can actually send data, you must call [control] **once**.
 * By wrapping the [ControllerView] and [GraphicsView] into a [ControlledGraphicsContainerView],
 * this is done automatically.
 */
class ControllerView : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Disable 4th dimensional seeker as that is a feature not implemented yet:
        findViewById<SeekBar>(R.id.seekerRotationQ).isEnabled = false
        findViewById<SeekBar>(R.id.seekerTranslationQ).isEnabled = false
    }

    /**
     * Instantiate controllers piping values from seek bars to [graphicsView] and
     * the [SharedRenderData.controlledGeometry].
     */
    fun control(graphicsView: GraphicsView) {

        // Control render grid options:
        findViewById<Switch>(R.id.renderOptionGrid).also {
            // Set render grid option to current checked state:
            graphicsView.renderGrid = it.isChecked
        }.setOnCheckedChangeListener { _, isChecked ->
            // Update the render grid option every times the checked state changes:
            graphicsView.renderGrid = isChecked
        }

        graphicsView.sharedRenderData.controlledGeometry.also {

            // Camera distance:
            cameraDistanceController(graphicsView.sharedRenderData, this)

            // X-Rotation:
            var oldXRotation = 0.0

            rotationController(
                    context,
                    findViewById(R.id.seekerRotationX),
                    findViewById(R.id.valueRotationX)
            ) { rotation ->
                it.rotateX(rotation - oldXRotation, Geometry.TransformApplyMode.PREPEND)
                oldXRotation = rotation
            }

            // Y-Rotation:
            var oldYRotation = 0.0

            rotationController(
                    context,
                    findViewById(R.id.seekerRotationY),
                    findViewById(R.id.valueRotationY)
            ) { rotation ->
                it.rotateY(rotation - oldYRotation, Geometry.TransformApplyMode.PREPEND)
                oldYRotation = rotation
            }

            // Z-Rotation:
            var oldZRotation = 0.0

            rotationController(
                    context,
                    findViewById(R.id.seekerRotationZ),
                    findViewById(R.id.valueRotationZ)
            ) { rotation ->
                it.rotateZ(rotation - oldZRotation, Geometry.TransformApplyMode.PREPEND)
                oldZRotation = rotation
            }

            // Q-Rotation:
            rotationController(
                    context,
                    findViewById(R.id.seekerRotationQ),
                    findViewById(R.id.valueRotationQ)
            ) {}

            // X-Translation:
            var oldXTranslation = 0.0

            translationController(
                    context,
                    findViewById(R.id.seekerTranslationX),
                    findViewById(R.id.valueTranslationX)
            ) { translation ->
                it.translateX(translation - oldXTranslation, Geometry.TransformApplyMode.PREPEND)
                oldXTranslation = translation
            }

            // Y-Translation:
            translationController(
                    context,
                    findViewById(R.id.seekerTranslationY),
                    findViewById(R.id.valueTranslationY)
            ) { }

            // Z-Translation:
            translationController(
                    context,
                    findViewById(R.id.seekerTranslationZ),
                    findViewById(R.id.valueTranslationZ)
            ) { }

            // Q-Translation:
            translationController(
                    context,
                    findViewById(R.id.seekerTranslationQ),
                    findViewById(R.id.valueTranslationQ)
            ) { }

        }

    }

}
