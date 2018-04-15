package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import org.junit.Test
import kotlin.math.PI

class SpatialTest {

    companion object {
        private const val maxModels = 10
    }

    /**
     * We simply store the global model matrices in the same contiguous memory section
     * as the local matrices.
     */
    private val matrixBuffer = MatrixBuffer(maxModels * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL))
    private val matrixMemory = matrixBuffer.MemorySpace()

    private var matrixOffset = 0
    private val spatials = ArrayList<Spatial>()

    private fun register(spatial: Spatial) {
        assertTrue("No more space to register new spatial", matrixOffset < matrixBuffer.maxMatrices)
        spatial.buffer = matrixBuffer
        spatial.matrixGlobal = matrixOffset
        spatial.matrixOffset = matrixOffset + 1
        matrixOffset += 1 + Spatial.LOCAL_MATRICES_PER_SPATIAL
    }

    @Test
    fun computeModelMatricesRecursively() {

        val root = Geometry("Root")
        val geometry = Geometry("Geometry")

        register(root)
        register(geometry)
        spatials.add(root)
        spatials.add(geometry)
        geometry.addToParentSpatial(root)

        root.rotationYX(PI / 2)
        geometry.translate(Vector(0.0, 1.0, 0.0, 1.0))
        root.computeModelMatricesRecursively()

        matrixMemory.multiply(Vector(0.0, 1.0, 0.0, 1.0), root.matrixGlobal).apply {
            assertEquals(1.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }

        matrixMemory.multiply(Vector(0.0, 1.0, 0.0, 1.0), geometry.matrixGlobal).apply {
            assertEquals(2.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }
    }

}
