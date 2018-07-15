/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.gl.Color
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Geometry unit test.
 */
class GeometryTest {
    
    @Test
    fun vertexPoints() {
        val a = Position(0.0, 0.0, 0.0, 0.0)
        val b = Position(1.0, 0.0, 0.0, 0.0)
        val c = Position(1.0, 1.0, 0.0, 0.0)
        val d = Position(0.0, 1.0, 0.0, 0.0)
        
        Geometry(
                "Test geometry",
                lines = quadrilateral(a, b, c, d),
                onTransformUpdate = { Transform() },
                color = Color.BLACK
        ).apply {
            
            assertEquals(8 * 4, positions.limit() - positions.position())
            
            for (i in positions.position() until positions.limit() / 4) {
                
                val expected = when (i) {
                    0 -> a
                    1 -> b
                    2 -> b
                    3 -> c
                    4 -> c
                    5 -> d
                    6 -> d
                    7 -> a
                    else -> throw RuntimeException("Impossible index: $i")
                }
                
                assertEquals(expected.x, positions[i * 4 + 0], 0.1)
                assertEquals(expected.y, positions[i * 4 + 1], 0.1)
                assertEquals(expected.z, positions[i * 4 + 2], 0.1)
                assertEquals(expected.q, positions[i * 4 + 3], 0.1)
                
            }
        }
    }
    
}
