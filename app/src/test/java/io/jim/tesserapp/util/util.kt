package io.jim.tesserapp.util

import io.jim.tesserapp.cpp.vector.VectorN
import junit.framework.Assert.assertEquals

@Suppress("unused")
fun assertEquals(expected: VectorN, actual: VectorN, delta: Double) {
    expected.components.forEachIndexed { index, float ->
        assertEquals(float, actual[index], delta)
    }
}

@Suppress("unused")
fun assertEquals(message: String, expected: VectorN, actual: VectorN, delta: Double) {
    expected.components.forEachIndexed { index, float ->
        assertEquals(message, float, actual[index], delta)
    }
}
