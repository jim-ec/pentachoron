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
            translation.x = 1f
            computeModelMatrix()
        }

        Vector4dh().apply {
            multiplication(
                    lhs = Vector4dh(1f, 0f, 0f, 0f),
                    rhs = geometry.modelMatrix
            )

            assertEquals(1f, x, 0.1f)
            assertEquals(1f, y, 0.1f)
            assertEquals(0f, z, 0.1f)
            assertEquals(0f, q, 0.1f)
        }
    }

    @Test
    fun extruding() {
        Geometry("Test", Color.BLACK).apply {
            addLine(Vector4dh(1f, 1f, 0f, 0f), Vector4dh(2f, 2f, 0f, 0f))
            extrude(Vector4dh(0f, 0f, 1f, 0f))
        }
    }

    @Test
    fun vertexPoints() {
        val a = Vector4dh(0f, 0f, 0f, 0f)
        val b = Vector4dh(1f, 0f, 0f, 0f)
        val c = Vector4dh(1f, 1f, 0f, 0f)
        val d = Vector4dh(0f, 1f, 0f, 0f)
        Quadrilateral("Test", a, b, c, d, Color.BLACK).apply {
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
                }, position, 0.1f)

                assertEquals(0f, red, 0.1f)
                assertEquals(0f, green, 0.1f)
                assertEquals(0f, blue, 0.1f)

                invocationCount++
            }

            assertEquals(8, invocationCount)
        }
    }

}

