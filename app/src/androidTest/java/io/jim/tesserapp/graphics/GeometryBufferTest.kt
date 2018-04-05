package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

class GeometryBufferTest {

    private val maxModels = 2
    private val geometryBuffer = GeometryBuffer(maxModels, 1000, 1000)

    @Test
    fun init() {
        val rotationSpatial = Spatial("Rotation Spatial")
        val translationSpatial = Geometry("Translation Geometry", Color(0f))

        translationSpatial.addToParentSpatial(rotationSpatial)
        geometryBuffer.recordGeometries(rotationSpatial, true)

        // Geometry buffer should have registered both spatials:
        assertEquals(rotationSpatial.matrixOffset, maxModels + 1)
        assertEquals(rotationSpatial.matrixGlobal, maxModels)
        assertEquals(translationSpatial.matrixOffset, maxModels + 1 + Spatial.LOCAL_MATRICES_PER_SPATIAL + 1)
        assertEquals(translationSpatial.matrixGlobal, 0)

        // Create some transform matrices and compute global matrices:
        rotationSpatial.rotationYX(PI / 2) // [ 0 -1 0 0 | 1 0 0 0 | 0 0 1 0 | 0 0 0 1 ]
        translationSpatial.translate(Vector(0.0, 1.0, 0.0, 1.0))
        rotationSpatial.computeModelMatricesRecursively()

        // Apply points to the global matrix from the geometry buffer:
        geometryBuffer.modelMatrices.multiply(Vector(0.0, 1.0, 0.0, 1.0), rotationSpatial.matrixGlobal).apply {
            assertEquals(1.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }
        geometryBuffer.modelMatrices.multiply(Vector(0.0, 1.0, 0.0, 1.0), translationSpatial.matrixGlobal).apply {
            assertEquals(2.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }

        // Release child from parent, offset data should stay:
        translationSpatial.releaseFromParentSpatial()
        assertEquals(translationSpatial.matrixOffset, maxModels + 1 + Spatial.LOCAL_MATRICES_PER_SPATIAL + 1)
        assertEquals(translationSpatial.matrixGlobal, 0)

        println(geometryBuffer)

        // Re-record spatials, child should now occupy the very first slots:
        geometryBuffer.recordGeometries(translationSpatial, true)
        assertEquals(translationSpatial.matrixOffset, maxModels + 1)
        assertEquals(translationSpatial.matrixGlobal, 0)

        println(geometryBuffer)

        // Recompute global matrix.
        // Translation matrix should be preserved, although the matrix location changed:
        translationSpatial.computeModelMatricesRecursively()

        assertEquals(1.0f, geometryBuffer.modelMatrices[0, 0, 0], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 0, 1], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 0, 2], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 0, 3], 0.1f)

        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 1, 0], 0.1f)
        assertEquals(1.0f, geometryBuffer.modelMatrices[0, 1, 1], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 1, 2], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 1, 3], 0.1f)

        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 2, 0], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 2, 1], 0.1f)
        assertEquals(1.0f, geometryBuffer.modelMatrices[0, 2, 2], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 2, 3], 0.1f)

        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 3, 0], 0.1f)
        assertEquals(1.0f, geometryBuffer.modelMatrices[0, 3, 1], 0.1f)
        assertEquals(0.0f, geometryBuffer.modelMatrices[0, 3, 2], 0.1f)
        assertEquals(1.0f, geometryBuffer.modelMatrices[0, 3, 3], 0.1f)

        geometryBuffer.modelMatrices.multiply(Vector(0.0, 1.0, 0.0, 1.0), translationSpatial.matrixGlobal).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }


    }

}
