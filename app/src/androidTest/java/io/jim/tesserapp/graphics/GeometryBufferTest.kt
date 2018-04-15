package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import org.junit.Test
import kotlin.math.PI

class GeometryBufferTest {

    private val maxModels = 2
    private val geometryBuffer = GeometryBuffer(maxModels, 1000, 1000)
    private val modelMatrixMemory = geometryBuffer.modelMatrices.MemorySpace()

    @Test
    fun init() {
        val rotationGeometry = Geometry("Rotation")
        val translationGeometry = Geometry("Translation")

        translationGeometry.addToParentGeometry(rotationGeometry)
        geometryBuffer.recordGeometries(rotationGeometry)

        // Geometry buffer should have registered both geometries:
        assertEquals(rotationGeometry.matrixOffset, maxModels + 1)
        assertEquals(rotationGeometry.matrixGlobal, maxModels)
        assertEquals(translationGeometry.matrixOffset, maxModels + 1 + Geometry.LOCAL_MATRICES_PER_GEOMETRY + 1)
        assertEquals(translationGeometry.matrixGlobal, 0)

        // Create some transform matrices and compute global matrices:
        rotationGeometry.rotationYX(PI / 2) // [ 0 -1 0 0 | 1 0 0 0 | 0 0 1 0 | 0 0 0 1 ]
        translationGeometry.translate(Vector(0.0, 1.0, 0.0, 1.0))
        rotationGeometry.computeModelMatricesRecursively()

        // Apply points to the global matrix from the geometry buffer:
        modelMatrixMemory.multiply(Vector(0.0, 1.0, 0.0, 1.0), rotationGeometry.matrixGlobal).apply {
            assertEquals(1.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }
        modelMatrixMemory.multiply(Vector(0.0, 1.0, 0.0, 1.0), translationGeometry.matrixGlobal).apply {
            assertEquals(2.0, x, 0.1)
            assertEquals(0.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }

        // Release child from parent, offset data should stay:
        translationGeometry.releaseFromParentGeometry()
        assertEquals(translationGeometry.matrixOffset, maxModels + 1 + Geometry.LOCAL_MATRICES_PER_GEOMETRY + 1)
        assertEquals(translationGeometry.matrixGlobal, 0)

        // Re-record geometries, child should now occupy the very first slots:
        geometryBuffer.recordGeometries(translationGeometry)
        assertEquals(translationGeometry.matrixOffset, maxModels + 1)
        assertEquals(translationGeometry.matrixGlobal, 0)

        // Recompute global matrix.
        // Translation matrix should be preserved, although the matrix location changed:
        translationGeometry.computeModelMatricesRecursively()

        assertEquals(1.0f, modelMatrixMemory[0, 0, 0], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 0, 1], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 0, 2], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 0, 3], 0.1f)

        assertEquals(0.0f, modelMatrixMemory[0, 1, 0], 0.1f)
        assertEquals(1.0f, modelMatrixMemory[0, 1, 1], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 1, 2], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 1, 3], 0.1f)

        assertEquals(0.0f, modelMatrixMemory[0, 2, 0], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 2, 1], 0.1f)
        assertEquals(1.0f, modelMatrixMemory[0, 2, 2], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 2, 3], 0.1f)

        assertEquals(0.0f, modelMatrixMemory[0, 3, 0], 0.1f)
        assertEquals(1.0f, modelMatrixMemory[0, 3, 1], 0.1f)
        assertEquals(0.0f, modelMatrixMemory[0, 3, 2], 0.1f)
        assertEquals(1.0f, modelMatrixMemory[0, 3, 3], 0.1f)

        modelMatrixMemory.multiply(Vector(0.0, 1.0, 0.0, 1.0), translationGeometry.matrixGlobal).apply {
            assertEquals(0.0, x, 0.1)
            assertEquals(2.0, y, 0.1)
            assertEquals(0.0, z, 0.1)
            assertEquals(1.0, w, 0.1)
        }


    }

}
