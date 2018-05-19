package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.common.Pi
import io.jim.tesserapp.math.vector.Vector4dh
import io.jim.tesserapp.util.assertEquals
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Geometry unit test.
 */
class GeometryTest {

    @Test
    fun computeModelMatrices() {

        val geometry = Geometry("Geometry").apply {
            rotation.z = Pi / 2
            translation.x = 1.0
            computeModelMatrix()
        }

        Vector4dh().apply {
            multiplication(
                    lhs = Vector4dh(1.0, 0.0, 0.0, 0.0),
                    rhs = geometry.modelMatrix
            )

            assertEquals(1.0, x, 0.1)
            assertEquals(1.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(0.0, q, 0.1)
        }
    }

    @Test
    fun extruding() {
        Geometry("Test", Color.BLACK).apply {
            addLine(Vector4dh(1.0, 1.0, 0.0, 0.0), Vector4dh(2.0, 2.0, 0.0, 0.0))
            extrude(Vector4dh(0.0, 0.0, 1.0, 0.0))
        }
    }

    @Test
    fun vertexPoints() {
        val a = Vector4dh(0.0, 0.0, 0.0, 0.0)
        val b = Vector4dh(1.0, 0.0, 0.0, 0.0)
        val c = Vector4dh(1.0, 1.0, 0.0, 0.0)
        val d = Vector4dh(0.0, 1.0, 0.0, 0.0)

        Geometry("Test").apply {

            addQuadrilateral(a, b, c, d)

            var invocationCount = 0

            forEachVertex { position, (red, green, blue) ->

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

                assertEquals(0.0, red, 0.1)
                assertEquals(0.0, green, 0.1)
                assertEquals(0.0, blue, 0.1)

                invocationCount++
            }

            assertEquals(8, invocationCount)
        }
    }

}

