package io.jim.tesserapp.graphics

import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorRes
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import io.jim.tesserapp.R

typealias ColorInt = Int

operator fun Int.component1() = redFloat(this)

operator fun Int.component2() = greenFloat(this)

operator fun Int.component3() = blueFloat(this)

/**
 * Opaque black.
 */
const val BLACK = android.graphics.Color.BLACK

/**
 * Construct a color from three RGB components, each in range [0,255].
 * 0 maps to 0.0, while 255 maps to 1.0, component-wise.
 */
fun colorInt(red: Int, green: Int, blue: Int) =
        android.graphics.Color.rgb(red, green, blue)

/**
 * Construct a color from a color-resource.
 * @param resource Color resource, like [R.color.accent].
 */
fun colorInt(context: Context, @ColorRes resource: Int) =
        ContextCompat.getColor(context, resource)

/**
 * Construct a color from a styleable color attribute.
 * @param styleRes Style applied to color attribute, like [R.style.AppTheme].
 * @param attr Color attribute to be styled, like [android.R.attr.windowBackground].
 */
fun colorInt(context: Context, @StyleRes styleRes: Int, @AttrRes attr: Int) =
        colorInt(context, context.theme
                .obtainStyledAttributes(styleRes, intArrayOf(attr))
                .getResourceId(0, 0))

/**
 * Extracts the red color component.
 * @return Red in `[0,1]`
 */
fun redFloat(color: ColorInt) = android.graphics.Color.red(color).toFloat() / 255f

/**
 * Extracts the green color component.
 * @return Green in `[0,1]`
 */
fun greenFloat(color: ColorInt) = android.graphics.Color.green(color).toFloat() / 255f

/**
 * Extracts the blue color component.
 * @return Blue in `[0,1]`
 */
fun blueFloat(color: ColorInt) = android.graphics.Color.blue(color).toFloat() / 255f
