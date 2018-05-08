package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.math.common.Pi
import io.jim.tesserapp.math.transform.Matrix
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

        Matrix.vector(4).apply {
            multiplication(
                    lhs = Matrix.vector(1f, 0f, 0f, 1f),
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
            addLine(Vector(1f, 1f, 0f, 1f), Vector(2f, 2f, 0f, 1f))
            extrude(Vector(0f, 0f, 1f, 0f))
        }
    }

    @Test
    fun clearLines() {
        Lines("Test", Color.BLACK).apply {
            addLine(Vector(1f, 1f, 0f, 1f), Vector(2f, 2f, 0f, 1f))
            clearLines()
        }
    }

    @Test
    fun vertexPoints() {
        val a = Vector(0f, 0f, 0f, 0f)
        val b = Vector(1f, 0f, 0f, 0f)
        val c = Vector(1f, 1f, 0f, 0f)
        val d = Vector(0f, 1f, 0f, 0f)
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

