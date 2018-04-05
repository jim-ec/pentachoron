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
     * The global model matrices of geometry which is actually drawn occupies the front memory,
     * so it can be uploaded without any memory moves.
     *
     * The rear memory section holds global model matrices of non-geometry spatial, as well as
     * all local and temporary matrices (translation and rotation).
     *
     * How exactly that rear section is laid out per spatial is defined by the [Spatial] class,
     * not by the [GeometryBuffer]. Spatials only specify how many matrices they need at least
     * in [Spatial.LOCAL_MATRICES_PER_SPATIAL], without counting the global matrix at all.
     *
     * Global model matrices which are kept in the rear memory section, i.e. not uploaded,
     * are kept in front of the spatial owned matrices.
     */
    var modelMatrices = MatrixBuffer(maxModels * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL + 1))

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
                modelMatrices.identity(i)
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

            if (spatial is Geometry && spatial.visible) {

                // This geometry is actually drawn an therefore gets a global model matrix slot
                // in the front memory section.
                // The count of matrices stored there is controlled by geometryModelMatrixCount:
                spatial.matrixGlobal = geometryModelMatrixCount

                // Copy all vertices from geometry into vertex buffer:
                for (point in spatial.points) {
                    vertexBuffer.appendVertex(point, spatial.color, geometryModelMatrixCount)
                }

                // Copy indices into index buffer:
                indexBuffer.recordGeometry(spatial)

                // Increment count of global model matrices, since this geometry used one:
                geometryModelMatrixCount++
            }
            else {

                // This spatial is not actually drawn, but still needs a slot to store its global
                // model matrix somewhere. So we store it in the rear section in front of this
                // spatials other local matrices:
                spatial.matrixGlobal = modelMatrixOffset

            }

            // Load old matrix data from this buffer into new one:
            // We don't need to copy the global matrix, since that is completely computed from
            // the local ones:
            if (loadOldMatrixData) {
                for (i in 0 until Spatial.LOCAL_MATRICES_PER_SPATIAL) {
                    newBuffer.copy(spatial.matrixOffset + i, oldMatrixOffset + i, modelMatrices)
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

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append("Geometry buffer with $modelCount of $maxModels models\n")

        sb.append("$geometryModelMatrixCount of $maxModels geometry model matrices:\n")

        for (i in 0 until maxModels) {
            sb.append("  Global [$i] #$i: ").append(modelMatrices.toString(i)).append('\n')
        }

        sb.append("$modelCount * ${Spatial.LOCAL_MATRICES_PER_SPATIAL + 1} = " +
                "${modelCount * (Spatial.LOCAL_MATRICES_PER_SPATIAL + 1)} local model matrices\n")

        for (m in 0 until maxModels) {
            sb.append("  Global [$m] " +
                    "#${maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL)}:     ")

            sb.append(modelMatrices.toString(maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL))).append('\n')

            for (i in 0 until Spatial.LOCAL_MATRICES_PER_SPATIAL) {
                sb.append("    Local [$m][$i] " +
                        "#${maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL) + 1 + i}: ")

                sb.append(modelMatrices.toString(maxModels + m * (1 + Spatial.LOCAL_MATRICES_PER_SPATIAL) + 1 + i)).append('\n')
            }
        }

        return sb.toString()
    }

}
