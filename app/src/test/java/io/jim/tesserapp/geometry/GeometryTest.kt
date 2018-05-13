package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.common.Pi
import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.math.vector.Vector4d
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

        Vector4d().apply {
            multiplication(
                    lhs = Vector4d(1f, 0f, 0f, 1f),
                    rhs = geometry.modelMatrix
            )

            assertEquals(1f, x, 0.1f)
            assertEquals(1f, y, 0.1f)
            assertEquals(0f, z, 0.1f)
            assertEquals(1f, q, 0.1f)
        }
    }

    @Test
    fun extruding() {
        Lines("Test", Color.BLACK).apply {
            addLine(Vector3d(1f, 1f, 0f), Vector3d(2f, 2f, 0f))
            extrude(Vector3d(0f, 0f, 1f))
        }
    }

    @Test
    fun clearLines() {
        Lines("Test", Color.BLACK).apply {
            addLine(Vector3d(1f, 1f, 0f), Vector3d(2f, 2f, 0f))
            clearLines()
        }
    }

    @Test
    fun vertexPoints() {
        val a = Vector3d(0f, 0f, 0f)
        val b = Vector3d(1f, 0f, 0f)
        val c = Vector3d(1f, 1f, 0f)
        val d = Vector3d(0f, 1f, 0f)
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

