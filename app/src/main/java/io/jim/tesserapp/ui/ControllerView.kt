package io.jim.tesserapp.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import io.jim.tesserapp.R
import kotlin.math.PI

/**
 * This view contains controls related to the coordinate system and its geometry.
 * But it does not host the coordinate system view instance itself.
 */
class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    /**
     * The set of GUI-controls set values through this interface.
     */
    interface Controllable {

        /**
         * Rotation around the y-axis.
         */
        var transformRotationXZ: Float

        /**
         * Rotation around the z-axis.
         */
        var transformRotationXY: Float

        /**
         * Translation along the x-axis.
         */
        var transformTranslationX: Float

        /**
         * Camera distance.
         */
        var transformCameraDistance: Float

        /**
         * Option whether to render the base-grid.
         */
        var renderGrid: Boolean
    }

    private val controllables = ArrayList<Controllable>()

    /**
     * Add a [controllable] to the list of targets this controller controls.
     */
    operator fun plusAssign(controllable: Controllable) {
        controllables += controllable
    }

    /**
     * Control for one rotation.
     */
    abstract inner class Control(
            private val seeker: SeekBar,
            private val valueLabel: TextView,
            private val min: Float,
            private val max: Float,
            startValue: Float = min
    ) {
        /**
         * Whenever the controlled value changed, this function is called for
         * each [controllable] registered at this time-point with the new [value].
         */
        protected abstract fun set(controllable: Controllable, value: Float)

        /**
         * Maps the seeker progress onto the [min]-[max] range.
         */
        protected val currentValue: Float
            get() = seeker.progress.toFloat() / seeker.max * (max - min) + min

        /**
         * The current value formatted into a string.
         */
        protected abstract val valueLabelText: String

        init {
            if (max < min)
                throw RuntimeException("Maximum must be greater than minimum")
            if (startValue < min || startValue > max)
                throw RuntimeException("Start value must be located in min-max range")

            seeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    valueLabel.text = valueLabelText
                    controllables.forEach {
                        set(it, currentValue)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            // Force the seeker bar to at least once call its value-changed listeners:
            seeker.progress = 1
            seeker.progress = ((startValue - min) / (max - min) * seeker.max).toInt()
        }
    }

    private val rotationControlXZ: Control
    private val rotationControlXY: Control
    private val translationControlX: Control
    private val cameraControlDistance: Control

    init {
        View.inflate(context, R.layout.view_controller, this)

        rotationControlXZ = object : Control(
                findViewById(R.id.seekerRotationXZ),
                findViewById(R.id.valueRotationXZ),
                0f, 2f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.transformRotationXZ = value * PI.toFloat()
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_rotation_value),
                        currentValue,
                        currentValue * 180.0
                )
        }

        rotationControlXY = object : Control(
                findViewById(R.id.seekerRotationXY),
                findViewById(R.id.valueRotationXY),
                0f, 2f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.transformRotationXY = value * PI.toFloat()
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_rotation_value),
                        currentValue,
                        currentValue * 180.0
                )
        }

        translationControlX = object : Control(
                findViewById(R.id.seekerTranslationX),
                findViewById(R.id.valueTranslationX),
                -5f, 5f, 0f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.transformTranslationX = value
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_translation_value),
                        currentValue
                )
        }

        cameraControlDistance = object : Control(
                findViewById(R.id.seekerCameraDistance),
                findViewById(R.id.valueCameraDistance),
                3f, 10f, 4f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.transformCameraDistance = value
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_translation_value),
                        currentValue
                )
        }

        // Pass render options:
        findViewById<Switch>(R.id.renderOptionGrid).setOnCheckedChangeListener { _, isChecked ->
            controllables.forEach {
                it.renderGrid = isChecked
            }
        }
    }

}
