package io.jim.tesserapp.math.common

/**
 * Provides a value that, if changed, transitions smoothly over time using an underlying curve.
 *
 * @param startValue Start value of property.
 * @param transitionTimeInterval Time it takes to reach a new value.
 */
open class SmoothTimedValueDelegate<R>(
        startValue: Float,
        transitionTimeInterval: Long
) : SmoothValueDelegate<R>(startValue, transitionTimeInterval) {

    /**
     * The axis start is defined as the construction time.
     */
    override val x0 = System.currentTimeMillis()

    /**
     * Axis progression is defined by the time passed since construction time.
     */
    override val x: Long
        get() = System.currentTimeMillis() - x0

}
