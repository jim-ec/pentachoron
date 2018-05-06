package io.jim.tesserapp.math

/**
 * Provides a value that, if changed, transitions smoothly over time using an underlying curve.
 */
class SmoothValue<R>(
        startValue: Float,
        transitionTimeInterval: Long
) : SmoothValueBase<R>(startValue, transitionTimeInterval) {

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
