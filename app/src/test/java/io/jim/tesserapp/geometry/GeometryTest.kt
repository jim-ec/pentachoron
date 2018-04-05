package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.util.assertEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

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

        root.rotationYX(PI / 2)
        geometry.translate(Vector(0.0, 1.0, 0.0, 1.0))
        root.computeModelMatricesRecursively()

        root.globalMemory!!.multiply(lhs = Vector(0.0, 1.0, 0.0, 1.0)).apply {
            assertEquals(1.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }

        geometry.globalMemory!!.multiply(lhs = Vector(0.0, 1.0, 0.0, 1.0)).apply {
            assertEquals(2.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }
    }

    @Test
    fun extruding() {
        Lines("Test", Color.BLACK).apply {
            addLine(Vector(1.0, 1.0, 0.0, 1.0), Vector(2.0, 2.0, 0.0, 1.0))
            extrude(Vector(0.0, 0.0, 1.0, 0.0))

            assertEquals(4, points.size)
            assertEquals(4, lines.size)
            assertEquals(1.0, points.last().z, 0.1)
        }
    }

    @Test
    fun clearLines() {
        Lines("Test", Color.BLACK).apply {
            addLine(Vector(1.0, 1.0, 0.0, 1.0), Vector(2.0, 2.0, 0.0, 1.0))
            clearLines()

            assertEquals(0, points.size)
            assertEquals(0, lines.size)
        }
    }

    @Test
    fun vertexPoints() {
        val a = Vector(0.0, 0.0, 0.0, 0.0)
        val b = Vector(1.0, 0.0, 0.0, 0.0)
        val c = Vector(1.0, 1.0, 0.0, 0.0)
        val d = Vector(0.0, 1.0, 0.0, 0.0)
        Quadrilateral("Test", a, b, c, d, Color.BLACK).apply {
            val resolvedIndices = vertexPoints
            assertEquals(8, resolvedIndices.size)
            assertEquals(a, resolvedIndices[0], 0.1)
            assertEquals(b, resolvedIndices[1], 0.1)
            assertEquals(b, resolvedIndices[2], 0.1)
            assertEquals(c, resolvedIndices[3], 0.1)
            assertEquals(c, resolvedIndices[4], 0.1)
            assertEquals(d, resolvedIndices[5], 0.1)
            assertEquals(d, resolvedIndices[6], 0.1)
            assertEquals(a, resolvedIndices[7], 0.1)
        }
    }

}

