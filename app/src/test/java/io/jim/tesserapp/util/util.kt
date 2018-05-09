package io.jim.tesserapp.util

import io.jim.tesserapp.math.vector.VectorN
import junit.framework.Assert.assertEquals

@Suppress("unused")
fun assertEquals(expected: VectorN, actual: VectorN, delta: Float) {
    expected.forEachIndexed { index, float ->
        assertEquals(float, actual[index], delta)
    }
}

@Suppress("unused")
fun assertEquals(message: String, expected: VectorN, actual: VectorN, delta: Float) {
    expected.forEachIndexed { index, float ->
        assertEquals(message, float, actual[index], delta)
    }
}
