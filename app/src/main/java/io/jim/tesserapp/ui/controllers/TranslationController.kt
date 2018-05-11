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
        min: Float = -5f,
        max: Float = 5f,
        startValue: Float = 0f,
        private val setTranslation: (controllable: Controllable, translation: Float) -> Unit
) : Controller(
        controllables,
        seeker,
        valueLabel,
        min,
        max,
        startValue,
        context.getString(R.string.transform_translation_value)
) {

    override fun set(controllable: Controllable, value: Float) {
        setTranslation(controllable, value)
    }

}
