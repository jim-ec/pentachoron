package io.jim.tesserapp.math.common

import org.junit.Assert.assertEquals
import org.junit.Test

class SmoothTimedValueDelegateTest {

    /**
     * Usually, a smooth value would use the [io.jim.tesserapp.math.common.Smoothed]
     * class, in order to bind the transition to time.
     * But this test must be more exact and in addition, using that class would require
     * us to use [Thread.sleep] in order to *wait* for the proper value.
     *
     * Instead of that, this tests implements its own smooth value class, providing the x-axis
     * progression based on a counter, which is not related to time at all but to a simple
     * integer counter. Therefore, curve steps are exact and do not depend on time to pass.
     */
    private inner class NonTimedSmoothed<R>(
            startValue: Double
    ) : Smoothed<R>(startValue, INTERVAL) {

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

    private var value by NonTimedSmoothed<SmoothTimedValueDelegateTest>(2.0)

    @Test
    fun initialValueIsStartValue() {
        assertEquals(2.0, value, 0.1)
    }

    @Test
    fun transition() {
        value = 5.0

        // Value starts rising over time:

        smoothValueStep++
        assertEquals(2.4, value, 0.1)

        smoothValueStep++
        assertEquals(3.5, value, 0.1)

        smoothValueStep++
        assertEquals(4.5, value, 0.1)

        smoothValueStep++
        assertEquals(5.0, value, 0.1)

        // After the interval, value should stay and not further follow the internal curve:
        smoothValueStep++
        assertEquals(5.0, value, 0.1)
    }

    @Test
    fun transitionWithInterruption() {
        value = 5.0

        // Value starts rising over time:

        smoothValueStep++
        assertEquals(2.4, value, 0.1)

        smoothValueStep++
        assertEquals(3.5, value, 0.1)

        // After half of the interval passed, we set a new value.
        // It should now take another interval to reach that value.
        // Though the current transition is discarded, the value change-velocity is kept.

        value = 0.0

        assertEquals(3.5, value, 0.1)

        smoothValueStep++
        assertEquals(3.5, value, 0.1)

        smoothValueStep++
        assertEquals(2.3, value, 0.1)

        smoothValueStep++
        assertEquals(0.75, value, 0.1)

        smoothValueStep++
        assertEquals(0.0, value, 0.1)

        // After the interval, value should stay and not further follow the internal curve:
        smoothValueStep++
        assertEquals(0.0, value, 0.1)
    }

}
