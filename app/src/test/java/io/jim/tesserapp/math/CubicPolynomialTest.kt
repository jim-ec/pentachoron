/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.math

import org.junit.Assert.assertEquals
import org.junit.Test

class CubicPolynomialTest {
    
    private val curve = CubicPolynomial().apply {
        span(
                sourceX = -2.0,
                sourceY = -1.0,
                targetX = 5.0,
                targetY = 1.0,
                sourceGradient = 1.5
        )
    }
    
    @Test
    fun function() {
        assertEquals(-1.0, curve(-2.0), 0.1)
        assertEquals(-0.3, curve(-1.5), 0.1)
        assertEquals(0.2, curve(-1.0), 0.1)
        assertEquals(0.6, curve(-0.5), 0.1)
        assertEquals(0.9, curve(0.0), 0.1)
        assertEquals(1.1, curve(0.5), 0.1)
        assertEquals(1.2, curve(1.0), 0.1)
        assertEquals(1.3, curve(1.5), 0.1)
        assertEquals(1.3, curve(2.0), 0.1)
        assertEquals(1.2, curve(2.5), 0.1)
        assertEquals(1.2, curve(3.0), 0.1)
        assertEquals(1.1, curve(3.5), 0.1)
        assertEquals(1.0, curve(4.0), 0.1)
        assertEquals(1.0, curve(4.5), 0.1)
        assertEquals(1.0, curve(5.0), 0.1)
    }
    
    @Test
    fun derivation() {
        assertEquals(1.5, curve.derivation(-2.0), 0.1)
        assertEquals(1.2, curve.derivation(-1.5), 0.1)
        assertEquals(0.9, curve.derivation(-1.0), 0.1)
        assertEquals(0.7, curve.derivation(-0.5), 0.1)
        assertEquals(0.5, curve.derivation(0.0), 0.1)
        assertEquals(0.3, curve.derivation(0.5), 0.1)
        assertEquals(0.1, curve.derivation(1.0), 0.1)
        assertEquals(0.0, curve.derivation(1.5), 0.1)
        assertEquals(0.0, curve.derivation(2.0), 0.1)
        assertEquals(-0.1, curve.derivation(2.5), 0.1)
        assertEquals(-0.1, curve.derivation(3.0), 0.1)
        assertEquals(-0.1, curve.derivation(3.5), 0.1)
        assertEquals(-0.1, curve.derivation(4.0), 0.1)
        assertEquals(0.0, curve.derivation(4.5), 0.1)
        assertEquals(0.0, curve.derivation(5.0), 0.1)
    }
    
    @Test
    fun extentWithSameResult() {
        curve.reSpan(sourceX = 0.0, targetX = 5.0, targetY = 1.0, keepSourceGradient = true)
        assertEquals(-1.0, curve(-2.0), 0.1)
        assertEquals(-0.3, curve(-1.5), 0.1)
        assertEquals(0.2, curve(-1.0), 0.1)
        assertEquals(0.6, curve(-0.5), 0.1)
        assertEquals(0.9, curve(0.0), 0.1)
        assertEquals(1.1, curve(0.5), 0.1)
        assertEquals(1.2, curve(1.0), 0.1)
        assertEquals(1.3, curve(1.5), 0.1)
        assertEquals(1.3, curve(2.0), 0.1)
        assertEquals(1.2, curve(2.5), 0.1)
        assertEquals(1.2, curve(3.0), 0.1)
        assertEquals(1.1, curve(3.5), 0.1)
        assertEquals(1.0, curve(4.0), 0.1)
        assertEquals(1.0, curve(4.5), 0.1)
        assertEquals(1.0, curve(5.0), 0.1)
    }
    
    @Test
    fun extentResultingInDifferentCurve() {
        curve.reSpan(sourceX = 0.0, targetX = 3.0, targetY = 0.0, keepSourceGradient = true)
        assertEquals(0.9, curve(0.0), 0.1)
        assertEquals(1.0, curve(0.5), 0.1)
        assertEquals(0.9, curve(1.0), 0.1)
        assertEquals(0.6, curve(1.5), 0.1)
        assertEquals(0.3, curve(2.0), 0.1)
        assertEquals(0.1, curve(2.5), 0.1)
        assertEquals(0.0, curve(3.0), 0.1)
    }
}
