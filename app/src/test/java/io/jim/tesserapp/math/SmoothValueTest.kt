package io.jim.tesserapp.math

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Tests smooth properties.
 * This test can take several seconds because the actual values must be tests over a certain
 * time span.
 */
class SmoothValueTest {

    private companion object {
        private const val INTERVAL = 1000L
        private const val HALF_INTERVAL = 500L
        private const val QUARTER_INTERVAL = 250L
    }

    private var value: Float by SmoothValue<SmoothValueTest>(
            startValue = 2f,
            transitionInterval = INTERVAL
    )

    @Test
    fun initialValueIsStartValue() {
        assertEquals(2f, value, 0.1f)
    }

    @Test
    fun transition() {
        value = 5f

        // Value starts rising over time:

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(2.4f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(3.5f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(4.5f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(5.0f, value, 0.1f)

        // After the interval, value should stay and not further follow the internal curve:
        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(5.0f, value, 0.1f)
    }

    @Test
    fun transitionWithInterruption() {
        value = 5f

        // Value starts rising over time:

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(2.4f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(3.5f, value, 0.1f)

        // After half of the interval passed, we set a new value.
        // It should now take another interval to reach that value.
        // Though the current transition is discarded, the value change-velocity is kept.

        value = 0f

        assertEquals(3.5f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(3.5f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(2.3f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(0.75f, value, 0.1f)

        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(0.0f, value, 0.1f)

        // After the interval, value should stay and not further follow the internal curve:
        Thread.sleep(QUARTER_INTERVAL)
        assertEquals(0.0f, value, 0.1f)
    }

}
