package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.Transform
import io.jim.tesserapp.cpp.matrix.RotationPlane
import io.jim.tesserapp.cpp.matrix.rotation
import io.jim.tesserapp.cpp.matrix.transformChain
import io.jim.tesserapp.cpp.matrix.translation
import io.jim.tesserapp.cpp.vector.VectorN
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
                    //rotation(5, RotationPlane.AROUND_Z, Math.PI / 2.0) *
                    //        translation(5, VectorN(1.0, 0.0, 0.0, 0.0))
                    Transform(
                            rotationZ = Math.PI / 2.0,
                            translationX = 1.0
                    )
                },
                lines = listOf())
    
        val matrix = transformChain(listOf(
                rotation(5, RotationPlane.AROUND_Z, geometry.onTransformUpdate().rotationZ),
                translation(5, VectorN(geometry.onTransformUpdate().translationX, 0.0, 0.0, 0.0))
        ))
    
        (VectorN(1.0, 0.0, 0.0, 0.0) * matrix).apply {
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
                lines = quadrilateral(a, b, c, d),
                onTransformUpdate = { Transform() }
        ).apply {
            var invocationCount = 0
        
            lines.forEach {
                it.points.forEach { position ->
    
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
            }
            
            assertEquals(8, invocationCount)
        }
    }
    
}
