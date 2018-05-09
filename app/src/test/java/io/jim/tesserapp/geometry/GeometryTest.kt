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
            vertices(modelIndex = -1).also {
                assertEquals(8, it.size)
                assertEquals(a, it[0].position, 0.1f)
                assertEquals(b, it[1].position, 0.1f)
                assertEquals(b, it[2].position, 0.1f)
                assertEquals(c, it[3].position, 0.1f)
                assertEquals(c, it[4].position, 0.1f)
                assertEquals(d, it[5].position, 0.1f)
                assertEquals(d, it[6].position, 0.1f)
                assertEquals(a, it[7].position, 0.1f)
            }
        }
    }

}

