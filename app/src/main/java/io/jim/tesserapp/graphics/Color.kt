package io.jim.tesserapp.graphics

import android.content.Context
import android.support.annotation.AttrRes
import android.support.annotation.ColorRes
import android.support.annotation.StyleRes
import android.support.v4.content.ContextCompat
import io.jim.tesserapp.R

/**
 * A color with red, green and blue values.
 * @property red Red component, in range [0.0,1.0].
 * @property green Green component, in range [0.0,1.0].
 * @property blue Blue component, in range [0.0,1.0].
 */
data class Color(var red: Double, var green: Double, var blue: Double) {

    /**
     * Construct a color from three RGB components, each in range [0,255].
     * 0 maps to 0.0, while 255 maps to 1.0, component-wise.
     */
    constructor(red: Int, green: Int, blue: Int) :
            this(red.toFloat() / 255.0, green.toFloat() / 255.0, blue.toFloat() / 255.0)

    /**
     * Extract ARGB color components from bytes in [argb].
     */
    private constructor(argb: Int) :
            this((argb shr 16) and 0xff, (argb shr 8) and 0xff, argb and 0xff)

    /**
     * Construct a color from a color-resource.
     * @param resource Color resource, like [R.color.accent].
     */
    constructor(context: Context, @ColorRes resource: Int) :
            this(ContextCompat.getColor(context, resource))

    /**
     * Construct a color from a styleable color attribute.
     * @param styleRes Style applied to color attribute, like [R.style.AppTheme].
     * @param attr Color attribute to be styled, like [android.R.attr.windowBackground].
     */
    constructor(context: Context, @StyleRes styleRes: Int, @AttrRes attr: Int) :
            this(context, context.theme
                    .obtainStyledAttributes(styleRes, intArrayOf(attr))
                    .getResourceId(0, 0))

    /**
     * Modify a color by luminance by multiplying all color components by [amount].
     *
     * @param amount
     * Values greater than 1 brighten the color,
     * while values smaller than 1 darken the color.
     */
    fun luminance(amount: Double) {
        red *= amount
        green *= amount
        blue *= amount
    }

    companion object {

        /**
         * A black color constant.
         */
        val BLACK = Color(0.0, 0.0, 0.0)

    }

}
