package io.jim.tesserapp.ui.view

import android.content.Context
import android.graphics.Color
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import io.jim.tesserapp.R

/**
 * Construct a color from a color attribute, styled by the context's theme.
 * @param attr Styled color attribute, e.g. [R.attr.colorAccent].
 * @param fallback Fallback color if [attr] is not defined or not a color resource.
 */
@ColorInt
fun Context.themedColorInt(
        @AttrRes attr: Int,
        @ColorInt fallback: Int = Color.BLACK
) =
        with(theme.obtainStyledAttributes(intArrayOf(attr))) {
            // Typed array obtained, try to get color-resource:
            getColor(0, fallback).also {
                // Recycle obtained styled attributes of theme:
                recycle()
            }
        }
