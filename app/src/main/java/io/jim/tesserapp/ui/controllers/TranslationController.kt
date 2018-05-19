package io.jim.tesserapp.ui.controllers

import android.content.Context
import android.widget.SeekBar
import android.widget.TextView
import io.jim.tesserapp.R

class TranslationController(
        controllables: List<Controllable>,
        context: Context,
        seeker: SeekBar,
        valueLabel: TextView,
        min: Double = -5.0,
        max: Double = 5.0,
        startValue: Double = 0.0,
        private val setTranslation: (controllable: Controllable, translation: Double) -> Unit
) : Controller(
        controllables,
        seeker,
        valueLabel,
        min,
        max,
        startValue,
        context.getString(R.string.transform_translation_value)
) {

    override fun set(controllable: Controllable, value: Double) {
        setTranslation(controllable, value)
    }

}
