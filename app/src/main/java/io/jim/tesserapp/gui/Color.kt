package io.jim.tesserapp.gui

import android.content.Context
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat

data class Color(val red: Float, val green: Float, val blue: Float) {

    constructor(greyScale: Float) :
            this(greyScale, greyScale, greyScale)

    constructor(red: Int, green: Int, blue: Int) :
            this(red.toFloat() / 255f, green.toFloat() / 255f, blue.toFloat() / 255f)

    private constructor(argb: Int) :
            this((argb shr 16) and 0xff, (argb shr 8) and 0xff, argb and 0xff)

    constructor(context: Context, @ColorRes resource: Int) :
            this(ContextCompat.getColor(context, resource))

}
