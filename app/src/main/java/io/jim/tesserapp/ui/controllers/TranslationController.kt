package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

class TranslationController(
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        min: Double = -5.0,
        max: Double = 5.0,
        startValue: Double = 0.0,
        private val setTranslation: (translation: Double) -> Unit
) : Controller(
        seeker,
        valueLabel,
        min,
        max,
        startValue,
        context.getString(R.string.transform_translation_value)
) {

    override fun set(value: Double) {
        setTranslation(value)
    }

}
