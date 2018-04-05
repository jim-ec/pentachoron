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
     * in [Spatial.MATRICES_PER_SPATIAL], without counting the global matrix at all.
     *
     * Global model matrices which are kept in the rear memory section, i.e. not uploaded,
     * are kept in front of the spatial owned matrices.
     */
    val modelMatrices = MatrixBuffer(maxModels * (1 + Spatial.MATRICES_PER_SPATIAL + 1))

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
     */
    fun recordGeometries(rootSpatial: Spatial) {

        indexBuffer.startRecording()

        // The section of memory where we store global matrices of drawn geometry, i.e. the start:
        geometryModelMatrixCount = 0

        // The section of memory where we store local matrices rather than global geometry matrices:
        var modelMatrixOffset = maxModels

        rootSpatial.forEachRecursive { spatial ->

            // Associate the spatial matrix buffer to the one owned by this instance:
            spatial.buffer = modelMatrices

            // Set offset of spatial where it can store its matrices.
            // Since the first matrix slot is eventually used as the global matrix,
            // the spatial gets only slots behind the first one:
            spatial.offset = modelMatrixOffset + 1

            if (spatial is Geometry) {

                // This geometry is actually drawn an therefore gets a global model matrix slot
                // in the front memory section.
                // The count of matrices stored there is controlled by geometryModelMatrixCount:
                spatial.matrixGlobal = geometryModelMatrixCount

                println("Spatial [${spatial.name}] get model index $geometryModelMatrixCount")

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

            // We increase the offset by the count of matrix slots consumed per spatial plus
            // one, which is the global matrix for non-geometry spatials:
            modelMatrixOffset += Spatial.MATRICES_PER_SPATIAL + 1

        }

        indexBuffer.endRecording()

    }

}
