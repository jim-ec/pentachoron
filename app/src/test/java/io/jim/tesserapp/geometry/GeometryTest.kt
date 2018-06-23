package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.matrix.RotationPlane
import io.jim.tesserapp.math.matrix.rotation
import io.jim.tesserapp.math.matrix.times
import io.jim.tesserapp.math.matrix.translation
import io.jim.tesserapp.math.vector.VectorN
import io.jim.tesserapp.util.assertEquals
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Geometry unit test.
 */
class GeometryTest {
    
    @Test
    fun computeModelMatrices() {
        
        val geometry = Geometry(
                name = "Test geometry",
                onTransformUpdate = {
                    rotation(5, RotationPlane.AROUND_Z, Math.PI / 2.0) *
                            translation(5, VectorN(1.0, 0.0, 0.0, 0.0))
                },
                points = listOf())
        
        (VectorN(1.0, 0.0, 0.0, 0.0) * geometry.onTransformUpdate()).apply {
            assertEquals(1.0, x, 0.1)
            assertEquals(1.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(0.0, q, 0.1)
        }
    }
    
    @Test
    fun vertexPoints() {
        val a = VectorN(0.0, 0.0, 0.0, 0.0)
        val b = VectorN(1.0, 0.0, 0.0, 0.0)
        val c = VectorN(1.0, 1.0, 0.0, 0.0)
        val d = VectorN(0.0, 1.0, 0.0, 0.0)
    
        Geometry(
                "Test geometry",
                points = quadrilateral(a, b, c, d)
        ).apply {
            var invocationCount = 0
            
            forEachVertex { position, _ ->
                
                assertEquals(when (invocationCount) {
                    0 -> a
                    1 -> b
                    2 -> b
                    3 -> c
                    4 -> c
                    5 -> d
                    6 -> d
                    7 -> a
                    else -> throw RuntimeException()
                }, position, 0.1)
                
                invocationCount++
            }
            
            assertEquals(8, invocationCount)
        }
    }
    
}
