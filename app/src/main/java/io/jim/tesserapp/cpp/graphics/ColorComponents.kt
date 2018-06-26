package io.jim.tesserapp.cpp.graphics

import android.graphics.Color

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
