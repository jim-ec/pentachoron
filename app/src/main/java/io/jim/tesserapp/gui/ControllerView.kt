package io.jim.tesserapp.gui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * This view contains controls related to the coordinate system and its geometry.
 * But it does not host the coordinate system view instance itself.
 */
class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    private val seekerRotationXZ: SeekBar
    private val valueRotationXZ: TextView

    /**
     * Fired when xy rotation changed.
     */
    lateinit var rotationXZListener: (Double) -> Unit

    /**
     * Fire when render grid option changed.
     */
    lateinit var renderGridOptionChangedListener: (Boolean) -> Unit

    init {
        View.inflate(context, R.layout.view_controller, this)

        valueRotationXZ = findViewById(R.id.valueRotationXZ)
        formatValueTextView(valueRotationXZ, 0.0)
        seekerRotationXZ = findViewById(R.id.seekerRotationXZ)
        seekerRotationXZ.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radiansOverPi = 2 * progress.toDouble() / seekerRotationXZ.max
                formatValueTextView(valueRotationXZ, radiansOverPi)
                rotationXZListener(radiansOverPi)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Pass render options:
        findViewById<Switch>(R.id.renderOptionGrid).setOnCheckedChangeListener { _, isChecked ->
            renderGridOptionChangedListener(isChecked)
        }
    }

    private fun formatValueTextView(view: TextView, radiansOverPi: Double) {
        view.text = String.format(context.getString(R.string.transform_rotation_value),
                radiansOverPi, radiansOverPi * 180.0)
    }

}
