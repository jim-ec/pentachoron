package io.jim.tesserapp.geometry

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
                color = SymbolicColor.PRIMARY
        ).apply {
            var invocationCount = 0
        
            lines.forEach {
                it.points.forEach { (x, y, z, q) ->
        
                    val expected = when (invocationCount) {
                        0 -> a
                        1 -> b
                        2 -> b
                        3 -> c
                        4 -> c
                        5 -> d
                        6 -> d
                        7 -> a
                        else -> throw RuntimeException()
                    }
        
                    assertEquals(expected.x, x, 0.1)
                    assertEquals(expected.y, y, 0.1)
                    assertEquals(expected.z, z, 0.1)
                    assertEquals(expected.q, q, 0.1)
    
                    invocationCount++
    
                }
            }
            
            assertEquals(8, invocationCount)
        }
    }
    
}
