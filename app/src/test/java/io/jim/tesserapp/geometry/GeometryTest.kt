package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Pi
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.util.assertEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Geometry unit test.
 */
class GeometryTest {

    companion object {
        private const val maxModels = 10
    }

    private val globalBuffer = MatrixBuffer(maxModels)
    private var activeGeometries = 0

    private fun register(geometry: Geometry) {
        assertTrue("No more  to register new geometry", activeGeometries < maxModels)
        geometry.globalMemory = globalBuffer.MemorySpace(activeGeometries, 1)
        activeGeometries++
    }

    @Test
    fun computeModelMatricesRecursively() {
        val root = Geometry("Root")
        val geometry = Geometry("Geometry")

        register(root)
        register(geometry)
        geometry.addToParentGeometry(root)

        root.rotation.z = Pi / 2
        geometry.translation.y = 1f
        root.computeModelMatricesRecursively()

        root.globalMemory!!.multiply(lhs = Vector(0f, 1f, 0f, 1f)).apply {
            assertEquals(-1f, x, 0.1f)
            assertEquals(0f, y, 0.1f)
            assertEquals(0f, z, 0.1f)
            assertEquals(1f, w, 0.1f)
        }

        geometry.globalMemory!!.multiply(lhs = Vector(0f, 1f, 0f, 1f)).apply {
            assertEquals(-2f, x, 0.1f)
            assertEquals(0f, y, 0.1f)
            assertEquals(0f, z, 0.1f)
            assertEquals(1f, w, 0.1f)
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
            register(this)
            vertices.also {
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

