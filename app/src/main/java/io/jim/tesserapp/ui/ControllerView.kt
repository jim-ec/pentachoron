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
         * Rotation around the x-axis.
         */
        var rotationX: Float

        /**
         * Rotation around the y-axis.
         */
        var rotationY: Float

        /**
         * Rotation around the z-axis.
         */
        var rotationZ: Float

        /**
         * Rotation around on the w-x plane.
         */
        var rotationW: Float

        /**
         * Translation along the x-axis.
         */
        var translationX: Float

        /**
         * Translation along the y-axis.
         */
        var translationY: Float

        /**
         * Translation along the w-axis.
         */
        var translationW: Float

        /**
         * Translation along the z-axis.
         */
        var translationZ: Float

        /**
         * Camera distance.
         */
        var cameraDistance: Float

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
    abstract inner class Controller(
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

    init {
        View.inflate(context, R.layout.view_controller, this)

        // Pass render options:
        findViewById<Switch>(R.id.renderOptionGrid).setOnCheckedChangeListener { _, isChecked ->
            controllables.forEach {
                it.renderGrid = isChecked
            }
        }

        // X-Rotation:
        object : Controller(
                findViewById(R.id.seekerRotationX),
                findViewById(R.id.valueRotationX),
                0f, 2f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.rotationX = value * PI.toFloat()
            }

            override val valueLabelText: String
                get() =
                    String.format(context.getString(R.string.transform_rotation_value_radians),
                            currentValue)
        }

        // Y-Rotation:
        object : Controller(
                findViewById(R.id.seekerRotationY),
                findViewById(R.id.valueRotationY),
                0f, 2f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.rotationY = value * PI.toFloat()
            }

            override val valueLabelText: String
                get() =
                    String.format(context.getString(R.string.transform_rotation_value_radians),
                            currentValue)
        }

        // Z-Rotation:
        object : Controller(
                findViewById(R.id.seekerRotationZ),
                findViewById(R.id.valueRotationZ),
                0f, 2f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.rotationZ = value * PI.toFloat()
            }

            override val valueLabelText: String
                get() =
                    String.format(context.getString(R.string.transform_rotation_value_radians),
                            currentValue)
        }

        // W-Rotation:
        object : Controller(
                findViewById(R.id.seekerRotationW),
                findViewById(R.id.valueRotationW),
                0f, 2f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.rotationW = value * PI.toFloat()
            }

            override val valueLabelText: String
                get() =
                    String.format(context.getString(R.string.transform_rotation_value_radians),
                            currentValue)
        }

        // X-Translation:
        object : Controller(
                findViewById(R.id.seekerTranslationX),
                findViewById(R.id.valueTranslationX),
                -5f, 5f, 0f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.translationX = value
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_translation_value),
                        currentValue
                )
        }

        // Y-Translation:
        object : Controller(
                findViewById(R.id.seekerTranslationY),
                findViewById(R.id.valueTranslationY),
                -5f, 5f, 0f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.translationY = value
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_translation_value),
                        currentValue
                )
        }

        // Z-Translation:
        object : Controller(
                findViewById(R.id.seekerTranslationZ),
                findViewById(R.id.valueTranslationZ),
                -5f, 5f, 0f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.translationZ = value
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_translation_value),
                        currentValue
                )
        }

        // W-Translation:
        object : Controller(
                findViewById(R.id.seekerTranslationW),
                findViewById(R.id.valueTranslationW),
                -5f, 5f, 1f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.translationW = value
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_translation_value),
                        currentValue
                )
        }

        // Camera distance:
        object : Controller(
                findViewById(R.id.seekerCameraDistance),
                findViewById(R.id.valueCameraDistance),
                3f, 15f, 9f
        ) {
            override fun set(controllable: Controllable, value: Float) {
                controllable.cameraDistance = value
                println("Set new camera distance to: $value")
            }

            override val valueLabelText: String
                get() = String.format(
                        context.getString(R.string.transform_translation_value),
                        currentValue
                )
        }
    }

}
