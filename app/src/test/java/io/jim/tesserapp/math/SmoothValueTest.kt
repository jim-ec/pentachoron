package io.jim.tesserapp.math

import org.junit.Assert.assertEquals
import org.junit.Test

class SmoothValueTest {

    /**
     * Usually, a smooth value would use the [SmoothValue] class, in order to bind the transition
     * to time. But this time must be more exact and in addition, using that class would require
     * us to use `Thread.sleep` in order to *wait* for the proper value.
     *
     * Instead of that, this tests implements its own smooth value class, providing the x-axis
     * progression based on a counter, which is not related to time at all but to a simple
     * integer counter. Therefore, curve steps are exact and do not depend on time to pass.
     */
    private inner class SmoothValueProvider<R>(
            startValue: Float
    ) : SmoothValueBase<R>(startValue, INTERVAL) {

        override val x0 = 0L

        override val x: Long
            get() {
                return smoothValueStep * STEP_INTERVAL
            }

    }

    private var smoothValueStep = 0L

    private companion object {
        private const val INTERVAL = 1000L
        private const val STEP_INTERVAL = 250L
    }

    private var value by SmoothValueProvider<SmoothValueTest>(2f)

    @Test
    fun initialValueIsStartValue() {
        assertEquals(2f, value, 0.1f)
    }

    @Test
    fun transition() {
        value = 5f

        // Value starts rising over time:

        smoothValueStep++
        assertEquals(2.4f, value, 0.1f)

        smoothValueStep++
        assertEquals(3.5f, value, 0.1f)

        smoothValueStep++
        assertEquals(4.5f, value, 0.1f)

        smoothValueStep++
        assertEquals(5.0f, value, 0.1f)

        // After the interval, value should stay and not further follow the internal curve:
        smoothValueStep++
        assertEquals(5.0f, value, 0.1f)
    }

    @Test
    fun transitionWithInterruption() {
        value = 5f

        // Value starts rising over time:

        smoothValueStep++
        assertEquals(2.4f, value, 0.1f)

        smoothValueStep++
        assertEquals(3.5f, value, 0.1f)

        // After half of the interval passed, we set a new value.
        // It should now take another interval to reach that value.
        // Though the current transition is discarded, the value change-velocity is kept.

        value = 0f

        assertEquals(3.5f, value, 0.1f)

        smoothValueStep++
        assertEquals(3.5f, value, 0.1f)

        smoothValueStep++
        assertEquals(2.3f, value, 0.1f)

        smoothValueStep++
        assertEquals(0.75f, value, 0.1f)

        smoothValueStep++
        assertEquals(0.0f, value, 0.1f)

        // After the interval, value should stay and not further follow the internal curve:
        smoothValueStep++
        assertEquals(0.0f, value, 0.1f)
    }

}
