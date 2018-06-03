package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

/**
 * A translation controller.
 * The current translation value is passed to [setTranslation].
 */
fun translationController(
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        setTranslation: (translation: Double) -> Unit
) = Controller(
        seekBar = seeker,
        watch = valueLabel,
        min = -5.0,
        max = 5.0,
        startValue = 0.0,
        formatString = context.getString(R.string.transform_translation_value),
        onValueUpdate = setTranslation
)
