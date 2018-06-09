package io.jim.tesserapp.graphics

import android.content.Context
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import io.jim.tesserapp.R

/**
 * Construct a color from a color attribute, styled by [context]'s theme.
 * @param attr Styled color attribute, e.g. [R.attr.colorAccent].
 * @param fallback Fallback color if [attr] is not defined or not a color resource.
 */
@ColorInt
fun themedColorInt(
        context: Context,
        @AttrRes attr: Int,
        @ColorInt fallback: Int = Color.BLACK
) =
        with(context.theme.obtainStyledAttributes(intArrayOf(attr))) {
            // Typed array obtained, try to get color-resource:
            getColor(0, fallback).also {
                // Recycle obtained styled attributes of theme:
                recycle()
            }
        }

/**
 * Extracts the red color component.
 * @return Red in `[0,1]`
 */
inline val Int.red: Float
    get() = Color.red(this).toFloat() / 255f

/**
 * Extracts the green color component.
 * @return Green in `[0,1]`
 */
inline val Int.green: Float
    get() = Color.green(this).toFloat() / 255f

/**
 * Extracts the blue color component.
 * @return Blue in `[0,1]`
 */
inline val Int.blue: Float
    get() = Color.blue(this).toFloat() / 255f

/**
 * Extracts the red color component.
 * @return Red in `[0,1]`
 */
operator fun Int.component1() = this.red

/**
 * Extracts the green color component.
 * @return Green in `[0,1]`
 */
operator fun Int.component2() = this.green

/**
 * Extracts the blue color component.
 * @return Blue in `[0,1]`
 */
operator fun Int.component3() = this.blue
