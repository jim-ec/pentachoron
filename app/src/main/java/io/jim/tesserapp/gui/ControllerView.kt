package io.jim.tesserapp.gui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import io.jim.tesserapp.R
import io.jim.tesserapp.util.ListenerListParam
import kotlin.math.PI

/**
 * This view contains controls related to the coordinate system and its geometry.
 * But it does not host the coordinate system view instance itself.
 */
class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    /**
     * Control for one rotation.
     */
    inner class RotationControl(
            private val seeker: SeekBar,
            private val currentValueLabel: TextView
    ) {
        /**
         * Callback fired when rotation changes.
         */
        val listeners = ListenerListParam<Float>()

        init {
            formatValueTextView(currentValueLabel, 0f)

            seeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val radiansOverPi = 2 * progress.toFloat() / seeker.max
                    formatValueTextView(currentValueLabel, radiansOverPi)
                    listeners.fire(radiansOverPi * PI.toFloat())
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
        }

        private fun formatValueTextView(view: TextView, radiansOverPi: Float) {
            view.text = String.format(context.getString(R.string.transform_rotation_value),
                    radiansOverPi, radiansOverPi * 180.0)
        }
    }

    /**
     * Control for translation.
     */
    inner class TranslationControl(
            private val seeker: SeekBar,
            private val currentValueLabel: TextView
    ) {
        /**
         * Callback fired when rotation changes.
         */
        val listeners = ListenerListParam<Float>()

        init {
            seeker.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    val translation = progress.toFloat() / seeker.max * 10 - 5
                    currentValueLabel.text = String.format(
                            context.getString(R.string.transform_translation_value),
                            translation)
                    listeners.fire(translation)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })
            seeker.progress = seeker.max / 2
        }
    }

    /**
     * Control for the xz rotation.
     */
    val rotationControlXZ: RotationControl

    /**
     * Control for the xy rotation.
     */
    val rotationControlXY: RotationControl

    /**
     * Control for the x translation.
     */
    val translationControlX: TranslationControl

    /**
     * Fire when render grid option changed.
     */
    lateinit var renderGridOptionChangedListener: (Boolean) -> Unit

    init {
        View.inflate(context, R.layout.view_controller, this)

        rotationControlXZ = RotationControl(
                findViewById(R.id.seekerRotationXZ),
                findViewById(R.id.valueRotationXZ)
        )

        rotationControlXY = RotationControl(
                findViewById(R.id.seekerRotationXY),
                findViewById(R.id.valueRotationXY)
        )

        translationControlX = TranslationControl(
                findViewById(R.id.seekerTranslationX),
                findViewById(R.id.valueTranslationX)
        )

        // Pass render options:
        findViewById<Switch>(R.id.renderOptionGrid).setOnCheckedChangeListener { _, isChecked ->
            renderGridOptionChangedListener(isChecked)
        }
    }

}
