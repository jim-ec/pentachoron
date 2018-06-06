package io.jim.tesserapp.graphics

import android.content.Context
import android.graphics.Color
import android.support.annotation.AttrRes
import android.support.annotation.ColorRes
import android.support.v4.content.ContextCompat
import io.jim.tesserapp.R

/**
 * Construct a color from a color-resource.
 * @param resource Color resource, like [R.color.lightAccent].
 */
fun colorInt(context: Context, @ColorRes resource: Int) =
        ContextCompat.getColor(context, resource)

/**
 * Construct a color from a styleable color attribute.
 * @param attr Color attribute to be styled, like [android.R.attr.windowBackground].
 */
fun themedColorInt(context: Context, @AttrRes attr: Int) =
        colorInt(context,
                with(context.theme.obtainStyledAttributes(intArrayOf(attr))) {
                    getResourceId(0, 0).also {
                        // Recycle obtained styled attributes of theme:
                        recycle()
                    }
                }
        )

/**
 * Extracts the red color component.
 * @return Red in `[0,1]`
 */
val Int.red: Float
    get() = Color.red(this).toFloat() / 255f

/**
 * Extracts the green color component.
 * @return Green in `[0,1]`
 */
val Int.green: Float
    get() = Color.green(this).toFloat() / 255f

/**
 * Extracts the blue color component.
 * @return Blue in `[0,1]`
 */
val Int.blue: Float
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
