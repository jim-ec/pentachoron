package io.jim.tesserapp.math

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Provides a value that, if changed, transitions smoothly over time using an underlying curve.
 */
class SmoothValue<R>(
        startValue: Float,
        private val transitionInterval: Long
) : ReadWriteProperty<R, Float> {

    private var curve = CubicPolynomial(y0 = startValue)

    private val t0 = System.currentTimeMillis()

    private val t: Long
        get() = System.currentTimeMillis() - t0

    private var lastTransitionStart = 0L

    private val transitioning: Boolean
        get() = t - lastTransitionStart < transitionInterval

    override fun setValue(thisRef: R, property: KProperty<*>, value: Float) {
        lastTransitionStart = t
        if (transitioning) {
            curve.reSpan(
                    sourceX = t.toFloat(),
                    targetX = (t + transitionInterval).toFloat(),
                    targetY = value,
                    keepSourceGradient = true
            )
        }
        else {
            curve.span(
                    sourceX = t.toFloat(),
                    sourceY = currentValue,
                    targetX = (t + transitionInterval).toFloat(),
                    targetY = value,
                    sourceGradient = 0f
            )
        }
    }

    val currentValue: Float
        get() = if (transitioning) {
            curve(t.toFloat())
        }
        else {
            curve((lastTransitionStart + transitionInterval).toFloat())
        }

    override fun getValue(thisRef: R, property: KProperty<*>) = currentValue

}
