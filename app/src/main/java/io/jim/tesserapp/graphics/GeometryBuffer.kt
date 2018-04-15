package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Spatial
import io.jim.tesserapp.math.MatrixBuffer

/**
 * A geometry buffer, responsible for vertex and index data.
 * How vertex, index and matrix data is laid out is completely up to this geometry buffer.
 */
class GeometryBuffer(private val maxModels: Int, maxVertices: Int, maxIndices: Int) {

    /**
     * Model matrix bulk buffer.
     *
     * While each geometry gets it own little memory space inside the matrix buffer where it has
     * access to its local matrices, all global matrices are kept in the front section of the
     * memory. That enables faster GPU uploads because the matrices don't have to be copied into
     * a new buffer to be contiguous.
     */
    var modelMatrices = MatrixBuffer(maxModels * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL))

    /**
     * Actual count of valid global model matrices.
     *
     * This is the count of matrices actually uploaded to shaders.
     */
    var geometryModelMatrixCount = 0
        private set

    /**
     * Return the count of indices actually stored and needed to be drawn.
     */
    val indexCount
        get() = indexBuffer.indexCounter

    private val vertexBuffer = VertexBuffer(maxVertices)
    private val indexBuffer = IndexBuffer(maxIndices)
    private var modelCount = 0

    /**
     * Bind the geometry buffers and re-instructs vertex attribute pointers of [shader].
     */
    fun bind(shader: Shader) {
        vertexBuffer.bind(shader)
        indexBuffer.bind()
    }

    /**
     * Store the geometries, whose root is denoted by [rootSpatial], into the vertex and index
     * buffers.
     *
     * Matrix data of spatials already registered to this buffer is preserved if enabled by
     * [preserveMatrixData]. Otherwise, all matrices are reset to the identity matrix.
     */
    fun recordGeometries(rootSpatial: Spatial, preserveMatrixData: Boolean) {

        modelCount = 0

        val newBuffer = if (preserveMatrixData)
            MatrixBuffer(modelMatrices.maxMatrices)
        else modelMatrices.also {
            // Flush matrix buffer:
            for (i in 0 until modelMatrices.maxMatrices) {
                modelMatrices.memory.identity(i)
            }
        }

        indexBuffer.startRecording()

        // The section of memory where we store global matrices of drawn geometry, i.e. the start:
        geometryModelMatrixCount = 0

        // The section of memory where we store local matrices rather than global geometry matrices:
        var modelMatrixOffset = maxModels

        rootSpatial.forEachRecursive { spatial ->

            val loadOldMatrixData = preserveMatrixData && spatial.buffer == modelMatrices
            val oldMatrixOffset = spatial.matrixOffset

            // Associate the spatial matrix buffer to the one owned by this instance:
            spatial.buffer = newBuffer

            // Set offset of spatial where it can store its matrices.
            // Since the first matrix slot is eventually used as the global matrix,
            // the spatial gets only slots behind the first one:
            spatial.matrixOffset = modelMatrixOffset + 1

            // This geometry is actually drawn an therefore gets a global model matrix slot
            // in the front memory section.
            // The count of matrices stored there is controlled by geometryModelMatrixCount:
            spatial.matrixGlobal = geometryModelMatrixCount

            if (spatial is Geometry && spatial.visible) {

                // Copy all vertices from geometry into vertex buffer:
                println("Geometry ${spatial.name} uploads ${spatial.points.size} vertices")
                for (point in spatial.points) {
                    vertexBuffer.appendVertex(point, spatial.color, geometryModelMatrixCount)
                }

                // Copy indices into index buffer:
                indexBuffer.recordGeometry(spatial)

                // Increment count of global model matrices, since this geometry used one:
                geometryModelMatrixCount++

            }
            /* else {

                // This spatial is not actually drawn, but still needs a slot to store its global
                // model matrix somewhere. So we store it in the rear section in front of this
                // spatials other local matrices:
                spatial.matrixGlobal = modelMatrixOffset

            } */

            // Load old matrix data from this buffer into new one:
            // We don't need to copy the global matrix, since that is completely computed from
            // the local ones:
            if (loadOldMatrixData) {
                for (i in 0 until Spatial.LOCAL_MATRICES_PER_SPATIAL) {
                    newBuffer.memory.copy(spatial.matrixOffset + i, oldMatrixOffset + i, modelMatrices.MemorySpace())
                }
            }

            // We increase the offset by the count of matrix slots consumed per spatial plus
            // one, which is the global matrix for non-geometry spatials:
            modelMatrixOffset += Spatial.LOCAL_MATRICES_PER_SPATIAL + 1

            modelCount++

        }

        indexBuffer.endRecording()

        modelMatrices = newBuffer

    }

    override fun toString() = let {
        val sb = StringBuilder()
        sb.append("Geometry buffer with $modelCount of $maxModels models\n")

        sb.append("$geometryModelMatrixCount of $maxModels geometry model matrices:\n")

        for (i in 0 until maxModels) {
            sb.append("  Global [$i] #$i: ").append(modelMatrices.memory.toString(i)).append('\n')
        }

        sb.append("$modelCount * ${Spatial.LOCAL_MATRICES_PER_SPATIAL + 1} = " +
                "${modelCount * (Spatial.LOCAL_MATRICES_PER_SPATIAL + 1)} local model matrices\n")

        for (m in 0 until maxModels) {
            sb.append("  Global [$m] " +
                    "#${maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL)}:     ")

            sb.append(modelMatrices.memory.toString(maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL))).append('\n')

            for (i in 0 until Spatial.LOCAL_MATRICES_PER_SPATIAL) {
                sb.append("    Local [$m][$i] " +
                        "#${maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL) + 1 + i}: ")

                sb.append(modelMatrices.memory.toString(maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL) + 1 + i)).append('\n')
            }
        }

        sb.toString()
    }

}
