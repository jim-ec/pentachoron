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
    var globalModelMatrixCount = 0

    /**
     * Return the count of indices actually stored and needed to be drawn.
     */
    val indexCount
        get() = indexBuffer.globalIndexCounter

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

        var geometryIndex = 0
        var modelMatrixOffset = maxModels
        globalModelMatrixCount = 0

        rootSpatial.forEachRecursive { spatial ->

            // Register model matrix for this spatial:
            spatial.buffer = modelMatrices
            spatial.offset = modelMatrixOffset + 1

            if (spatial is Geometry) {

                spatial.matrixGlobal = globalModelMatrixCount++

                println("Spatial [${spatial.name}] get model index $geometryIndex")

                for (point in spatial.points) {
                    vertexBuffer.appendVertex(point, spatial.color, geometryIndex)
                }

                indexBuffer.recordGeometry(spatial)

                geometryIndex++
            }
            else {

                spatial.matrixGlobal = modelMatrixOffset

            }

            modelMatrixOffset += Spatial.MATRICES_PER_SPATIAL + 1

        }

        indexBuffer.endRecording()
    }

}
