package io.jim.tesserapp.math

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Provides a value that, if changed, transitions smoothly over a generic axis.
 * [SmoothValue] implements time as that axis.
 */
abstract class SmoothValueBase<R>(
        startValue: Float,
        private val transitionIntervalX: Long
) : ReadWriteProperty<R, Float> {

    /**
     * Override this property to provide a axis start position.
     * This can be e.g. the time point at which a smooth timed property was constructed.
     */
    protected abstract val x0: Long

    /**
     * The current axis progression. This would e.g. depend on the time passed for a smooth timed
     * property.
     */
    protected abstract val x: Long

    /**
     * The underlying curve used to model transitions.
     * Though the count of transitions is not limited, the curve is allocated only once and
     * then reused.
     */
    private var curve = CubicPolynomial(y0 = startValue)

    /**
     * The last axis position a transition started.
     */
    private var lastTransitionStartX = 0L

    /**
     * Whether the value is currently transitioning.
     *
     * A smooth value is transitioning initially and does that until the first interval
     * after a call to [setValue] has ended! But since the curve is initially flat, mapping
     * all x to 0, this is effectively the same behaviour like taking the value right after the
     * last transition interval has ended like it is done after the very first transition finished.
     */
    private val transitioning: Boolean
        get() = x - lastTransitionStartX < transitionIntervalX

    /**
     * The current value.
     *
     * Either the value is currently inside a transition interval, indicated by [transitioning],
     * in which case the value is simply the current [x] mapped by the [curve] to some value,
     * or the last interval has already ended. In that case, the x at which the last interval ended
     * is mapped by [curve] and therefore does not change anymore.
     */
    private val currentValue: Float
        get() = if (transitioning) {
            curve(x.toFloat())
        }
        else {
            curve((lastTransitionStartX + transitionIntervalX).toFloat())
        }

    override fun setValue(thisRef: R, property: KProperty<*>, value: Float) {
        lastTransitionStartX = x
        if (transitioning) {
            curve.reSpan(
                    sourceX = x.toFloat(),
                    targetX = (x + transitionIntervalX).toFloat(),
                    targetY = value,
                    keepSourceGradient = true
            )
        }
        else {
            curve.span(
                    sourceX = x.toFloat(),
                    sourceY = currentValue,
                    targetX = (x + transitionIntervalX).toFloat(),
                    targetY = value,
                    sourceGradient = 0f
            )
        }
    }

    override fun getValue(thisRef: R, property: KProperty<*>) = currentValue

}
