package io.jim.tesserapp.util

import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals

fun assertEquals(expected: Vector, actual: Vector, delta: Double) {
    assertEquals(expected.x, actual.x, delta)
    assertEquals(expected.y, actual.y, delta)
    assertEquals(expected.z, actual.z, delta)
    assertEquals(expected.w, actual.w, delta)
}

fun assertEquals(message: String, expected: Vector, actual: Vector, delta: Double) {
    assertEquals(message, expected.x, actual.x, delta)
    assertEquals(message, expected.y, actual.y, delta)
    assertEquals(message, expected.z, actual.z, delta)
    assertEquals(message, expected.w, actual.w, delta)
}

class FOO