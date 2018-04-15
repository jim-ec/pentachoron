package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import junit.framework.Assert
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

/**
 * Geometry unit test.
 */
class GeometryTest {

    companion object {
        private const val maxModels = 10
    }

    /**
     * We simply store the global model matrices in the same contiguous memory section
     * as the local matrices.
     */
    private val matrixBuffer = MatrixBuffer(maxModels * (1 + Geometry.LOCAL_MATRICES_PER_GEOMETRY))
    private val matrixMemory = matrixBuffer.MemorySpace()

    private var matrixOffset = 0
    private val geometries = ArrayList<Geometry>()

    private fun register(geometry: Geometry) {
        Assert.assertTrue("No more  to register new geometry", matrixOffset < matrixBuffer.maxMatrices)
        geometry.memory = matrixMemory
        geometry.matrixGlobal = matrixOffset
        geometry.matrixOffset = matrixOffset + 1
        matrixOffset += Geometry.LOCAL_MATRICES_PER_GEOMETRY
    }

    @Test
    fun computeModelMatricesRecursively() {

        val root = Geometry("Root")
        val geometry = Geometry("Geometry")

        register(root)
        register(geometry)
        geometries.add(root)
        geometries.add(geometry)
        geometry.addToParentGeometry(root)

        root.rotationYX(PI / 2)
        geometry.translate(Vector(0.0, 1.0, 0.0, 1.0))
        root.computeModelMatricesRecursively()

        matrixMemory.multiply(Vector(0.0, 1.0, 0.0, 1.0), root.matrixGlobal).apply {
            Assert.assertEquals(1.0, x, 0.1)
            Assert.assertEquals(0.0, y, 0.1)
            Assert.assertEquals(0.0, z, 0.1)
            Assert.assertEquals(1.0, w, 0.1)
        }

        matrixMemory.multiply(Vector(0.0, 1.0, 0.0, 1.0), geometry.matrixGlobal).apply {
            Assert.assertEquals(2.0, x, 0.1)
            Assert.assertEquals(0.0, y, 0.1)
            Assert.assertEquals(0.0, z, 0.1)
            Assert.assertEquals(1.0, w, 0.1)
        }
    }

    @Test
    fun extruding() {
        val geometry = Lines("Test", Color(0f))
        geometry.addLine(Vector(1.0, 1.0, 0.0, 1.0), Vector(2.0, 2.0, 0.0, 1.0))
        geometry.extrude(Vector(0.0, 0.0, 1.0, 0.0))
        assertEquals(4, geometry.points.size)
        assertEquals(4, geometry.lines.size)
        assertEquals(1.0, geometry.points.last().z, 0.1)
    }

}
