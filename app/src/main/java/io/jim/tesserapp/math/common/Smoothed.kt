package io.jim.tesserapp.math.common

import io.jim.tesserapp.math.common.Smoothed.DelegationMode
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Provides a value that, if changed, transitions smoothly over x-axis defined as time.
 *
 * @param startValue
 * The start the property should has.
 *
 * @property transitionInterval
 * The time it should take to fulfil one transition interval.
 *
 * @property delegationMode
 * Mode of delegation, see [DelegationMode].
 */
open class Smoothed<R>(
        startValue: Double,
        private val transitionInterval: Double,
        private val delegationMode: DelegationMode = DelegationMode.ABSOLUTE
) : ReadWriteProperty<R, Double> {

    /**
     * The axis start is defined as the construction time.
     */
    open val x0 = System.currentTimeMillis().toDouble()

    /**
     * Axis progression is defined by the time passed since construction time.
     */
    open val x: Double
        get() = System.currentTimeMillis() - x0

    /**
     * The underlying curve used to model transitions.
     * Though the count of transitions is not limited, the curve is allocated only once and
     * then reused.
     */
    private var curve = CubicPolynomial(y0 = startValue)

    /**
     * The last axis position a transition started.
     */
    private var lastTransitionStartX = 0.0

    /**
     * Whether the value is currently transitioning.
     *
     * A smooth value is transitioning initially and does that until the first interval
     * after a call to [setValue] has ended! But since the curve is initially flat, mapping
     * all x to 0, this is effectively the same behaviour like taking the value right after the
     * last transition interval has ended like it is done after the very first transition finished.
     */
    private inline val transitioning: Boolean
        get() = x - lastTransitionStartX < transitionInterval

    /**
     * Only used when delegating with [DelegationMode.RELATIVE_TO_LAST_READ].
     * Store the value existed when the last read operation occurred,
     * so we can return the difference.
     */
    private var oldValue = startValue

    /**
     * The current value.
     *
     * Either the value is currently inside a transition interval, indicated by [transitioning],
     * in which case the value is simply the current [x] mapped by the [curve] to some value,
     * or the last interval has already ended. In that case, the x at which the last interval ended
     * is mapped by [curve] and therefore does not change anymore.
     */
    private inline val currentValue: Double
        get() =
            if (transitioning)
                curve(x)
            else
                curve(lastTransitionStartX + transitionInterval)

    /**
     * Trigger a new transition interval.
     * If this value is currently transitioning, the new transition will keep the current
     * value change-rate, i.e. it will not restart from zero.
     */
    override fun setValue(thisRef: R, property: KProperty<*>, value: Double) {

        // If currently transitioning, re-span the curve from the current point of progression:
        if (transitioning) {
            curve.reSpan(
                    sourceX = x,
                    targetX = x + transitionInterval,
                    targetY = value,
                    keepSourceGradient = true
            )
        }

        // Otherwise, span a completely new curve, using final value from the last transition:
        else {
            curve.span(
                    sourceX = x,
                    sourceY = currentValue,
                    targetX = x + transitionInterval,
                    targetY = value,
                    sourceGradient = 0.0
            )
        }

        // Mark the transition start:
        lastTransitionStartX = x
    }

    /**
     * Get the current value.
     */
    override fun getValue(thisRef: R, property: KProperty<*>) =
            currentValue.let { value ->

                // Return difference, but remember the current value as the new old value:
                when (delegationMode) {
                    DelegationMode.ABSOLUTE -> value
                    DelegationMode.RELATIVE_TO_LAST_READ -> (value - oldValue).also { oldValue = value }
                }
            }

    /**
     * How the value is delegated when read.
     */
    enum class DelegationMode {

        /**
         * Each read will return current absolute value.
         * This is the default behaviour you would expect.
         */
        ABSOLUTE,

        /**
         * Each read operation will return the *difference* between the current value
         * and the value hold when the last read operation occurred.
         *
         * This is more useful when you want to listen how the value has changed,
         * rather than what its current absolute value is.
         *
         * Since every read operation affects the next read operation,
         * you have to define explicitly who can read the property at all.
         *
         * I.e. to keep track of the absolute value, you'd have to accumulate all read retrievals.
         */
        RELATIVE_TO_LAST_READ
    }

}
