package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Switch
import io.jim.tesserapp.R
import io.jim.tesserapp.ui.controllers.CameraDistanceController
import io.jim.tesserapp.ui.controllers.RotationController
import io.jim.tesserapp.ui.controllers.TranslationController


/**
 * This view contains controls related to the coordinate system and its geometry.
 * But it does not host the coordinate system view instance itself.
 */
class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Disable 4th dimensional seeker as that is a feature not implemented yet:
        findViewById<SeekBar>(R.id.seekerRotationQ).isEnabled = false
        findViewById<SeekBar>(R.id.seekerTranslationQ).isEnabled = false
    }

    fun control(graphicsView: GraphicsView) {

        // Control render grid options:
        findViewById<Switch>(R.id.renderOptionGrid).also {
            // Set render grid option to current checked state:
            graphicsView.renderGrid = it.isChecked
        }.setOnCheckedChangeListener { _, isChecked ->
            // Update the render grid option every times the checked state changes:
            graphicsView.renderGrid = isChecked
        }

        val controlledGeometry = graphicsView.sharedRenderData.controlledGeometry

        // X-Rotation:
        RotationController(
                context,
                findViewById(R.id.seekerRotationX),
                findViewById(R.id.valueRotationX)
        ) { rotation ->
            controlledGeometry.smoothRotation.x = rotation
        }

        // Y-Rotation:
        RotationController(
                context,
                findViewById(R.id.seekerRotationY),
                findViewById(R.id.valueRotationY)
        ) { rotation ->
            controlledGeometry.smoothRotation.y = rotation
        }

        // Z-Rotation:
        RotationController(
                context,
                findViewById(R.id.seekerRotationZ),
                findViewById(R.id.valueRotationZ)
        ) { rotation ->
            controlledGeometry.smoothRotation.z = rotation
        }

        // Q-Rotation:
        RotationController(
                context,
                findViewById(R.id.seekerRotationQ),
                findViewById(R.id.valueRotationQ)
        ) { rotation ->
            controlledGeometry.smoothRotation.q = rotation
        }

        // X-Translation:
        TranslationController(
                context,
                findViewById(R.id.seekerTranslationX),
                findViewById(R.id.valueTranslationX)
        ) { translation ->
            controlledGeometry.smoothTranslation.x = translation
        }

        // Y-Translation:
        TranslationController(
                context,
                findViewById(R.id.seekerTranslationY),
                findViewById(R.id.valueTranslationY)
        ) { translation ->
            controlledGeometry.smoothTranslation.y = translation
        }

        // Z-Translation:
        TranslationController(
                context,
                findViewById(R.id.seekerTranslationZ),
                findViewById(R.id.valueTranslationZ)
        ) { translation ->
            controlledGeometry.smoothTranslation.z = translation
        }

        // W-Translation:
        TranslationController(
                context,
                findViewById(R.id.seekerTranslationQ),
                findViewById(R.id.valueTranslationQ)
        ) { translation ->
            controlledGeometry.smoothTranslation.q = translation
        }

        CameraDistanceController(
                graphicsView.sharedRenderData,
                context,
                findViewById(R.id.seekerCameraDistance),
                findViewById(R.id.valueCameraDistance)
        )

    }

}
