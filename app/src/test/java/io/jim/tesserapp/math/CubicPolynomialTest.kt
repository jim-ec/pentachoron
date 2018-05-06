package io.jim.tesserapp.math

import org.junit.Assert.assertEquals
import org.junit.Test

class CubicPolynomialTest {

    private val curve = CubicPolynomial.parametrized(
            sourceX = -2f,
            sourceY = -1f,
            targetX = 5f,
            targetY = 1f,
            sourceGradient = 1.5f
    )

    @Test
    fun function() {
        assertEquals(-1.0f, curve(-2.0f), 0.1f)
        assertEquals(-0.3f, curve(-1.5f), 0.1f)
        assertEquals(0.2f, curve(-1.0f), 0.1f)
        assertEquals(0.6f, curve(-0.5f), 0.1f)
        assertEquals(0.9f, curve(0.0f), 0.1f)
        assertEquals(1.1f, curve(0.5f), 0.1f)
        assertEquals(1.2f, curve(1.0f), 0.1f)
        assertEquals(1.3f, curve(1.5f), 0.1f)
        assertEquals(1.3f, curve(2.0f), 0.1f)
        assertEquals(1.2f, curve(2.5f), 0.1f)
        assertEquals(1.2f, curve(3.0f), 0.1f)
        assertEquals(1.1f, curve(3.5f), 0.1f)
        assertEquals(1.0f, curve(4.0f), 0.1f)
        assertEquals(1.0f, curve(4.5f), 0.1f)
        assertEquals(1.0f, curve(5.0f), 0.1f)
    }

    @Test
    fun derivation() {
        assertEquals(1.5f, curve.derivation(-2.0f), 0.1f)
        assertEquals(1.2f, curve.derivation(-1.5f), 0.1f)
        assertEquals(0.9f, curve.derivation(-1.0f), 0.1f)
        assertEquals(0.7f, curve.derivation(-0.5f), 0.1f)
        assertEquals(0.5f, curve.derivation(0.0f), 0.1f)
        assertEquals(0.3f, curve.derivation(0.5f), 0.1f)
        assertEquals(0.1f, curve.derivation(1.0f), 0.1f)
        assertEquals(0.0f, curve.derivation(1.5f), 0.1f)
        assertEquals(0.0f, curve.derivation(2.0f), 0.1f)
        assertEquals(-0.1f, curve.derivation(2.5f), 0.1f)
        assertEquals(-0.1f, curve.derivation(3.0f), 0.1f)
        assertEquals(-0.1f, curve.derivation(3.5f), 0.1f)
        assertEquals(-0.1f, curve.derivation(4.0f), 0.1f)
        assertEquals(0.0f, curve.derivation(4.5f), 0.1f)
        assertEquals(0.0f, curve.derivation(5.0f), 0.1f)
    }

    @Test
    fun extentWithSameResult() {
        curve.extent(sourceX = 0f, targetX = 5f, targetY = 1f, keepSourceGradient = true)
        assertEquals(-1.0f, curve(-2.0f), 0.1f)
        assertEquals(-0.3f, curve(-1.5f), 0.1f)
        assertEquals(0.2f, curve(-1.0f), 0.1f)
        assertEquals(0.6f, curve(-0.5f), 0.1f)
        assertEquals(0.9f, curve(0.0f), 0.1f)
        assertEquals(1.1f, curve(0.5f), 0.1f)
        assertEquals(1.2f, curve(1.0f), 0.1f)
        assertEquals(1.3f, curve(1.5f), 0.1f)
        assertEquals(1.3f, curve(2.0f), 0.1f)
        assertEquals(1.2f, curve(2.5f), 0.1f)
        assertEquals(1.2f, curve(3.0f), 0.1f)
        assertEquals(1.1f, curve(3.5f), 0.1f)
        assertEquals(1.0f, curve(4.0f), 0.1f)
        assertEquals(1.0f, curve(4.5f), 0.1f)
        assertEquals(1.0f, curve(5.0f), 0.1f)
    }

    @Test
    fun extentResultingInDifferentCurve() {
        curve.extent(sourceX = 0f, targetX = 3f, targetY = 0f, keepSourceGradient = true)
        assertEquals(0.9f, curve(0.0f), 0.1f)
        assertEquals(1.0f, curve(0.5f), 0.1f)
        assertEquals(0.9f, curve(1.0f), 0.1f)
        assertEquals(0.6f, curve(1.5f), 0.1f)
        assertEquals(0.3f, curve(2.0f), 0.1f)
        assertEquals(0.1f, curve(2.5f), 0.1f)
        assertEquals(0.0f, curve(3.0f), 0.1f)
    }

}
