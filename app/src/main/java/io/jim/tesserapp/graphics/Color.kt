package io.jim.tesserapp.graphics

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

/**
 * A color with red, green and blue values.
 */
data class Color(

        /**
         * The red component.
         */
        val red: Float,

        /**
         * The green component.
         */
        val green: Float,

        /**
         * The blue component.
         */
        val blue: Float) {

    constructor(greyScale: Float) :
            this(greyScale, greyScale, greyScale)

    constructor(red: Int, green: Int, blue: Int) :
            this(red.toFloat() / 255f, green.toFloat() / 255f, blue.toFloat() / 255f)

    private constructor(argb: Int) :
            this((argb shr 16) and 0xff, (argb shr 8) and 0xff, argb and 0xff)

    constructor(context: Context, @ColorRes resource: Int) :
            this(ContextCompat.getColor(context, resource))

    companion object {

        /**
         * A black color constant.
         */
        val BLACK = Color(0f, 0f, 0f)

    }

}
