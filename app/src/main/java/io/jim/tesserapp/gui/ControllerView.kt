package io.jim.tesserapp.gui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

class ControllerView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    val seekerRotationXZ: SeekBar
    val valueRotationXZ: TextView
    lateinit var rotationXZListener: (Double) -> Unit

    init {
        View.inflate(context, R.layout.view_controller, this)

        valueRotationXZ = findViewById(R.id.valueRotationXZ)
        formatValueTextView(valueRotationXZ, 0.0)
        seekerRotationXZ = findViewById(R.id.seekerRotationXZ)
        seekerRotationXZ.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radiansOverPi = progress / 100.0
                formatValueTextView(valueRotationXZ, radiansOverPi)
                rotationXZListener(radiansOverPi)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun formatValueTextView(view: TextView, radiansOverPi: Double) {
        view.text = String.format(context.getString(R.string.transform_rotation_value), radiansOverPi, radiansOverPi * 180.0)
    }

}
