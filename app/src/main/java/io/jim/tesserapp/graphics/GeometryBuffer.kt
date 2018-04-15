package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
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
    val modelMatrixBuffer = MatrixBuffer(maxModels * (1 + Geometry.LOCAL_MATRICES_PER_GEOMETRY))

    private val modelMatrixMemory = modelMatrixBuffer.MemorySpace()

    /**
     * Actual count of valid global model matrices.
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
     * Store the geometries, whose root is denoted by [rootGeometry], into the vertex and index
     * buffers.
     */
    fun recordGeometries(rootGeometry: Geometry) {

        modelCount = 0

        modelMatrixBuffer.also {
            // Flush matrix buffer (We currently need to do this, since the count an therefore the
            // order of geometries could be changed):
            for (i in 0 until modelMatrixBuffer.maxMatrices) {
                modelMatrixMemory.identity(i)
            }
        }

        indexBuffer.startRecording()

        // The section of memory where we store global matrices of drawn geometry, i.e. the start:
        geometryModelMatrixCount = 0

        // The section of memory where we store local matrices rather than global geometry matrices:
        var modelMatrixOffset = maxModels

        rootGeometry.forEachRecursive {

            it.localMemory = modelMatrixBuffer.MemorySpace(modelMatrixOffset)
            it.globalMemory = modelMatrixBuffer.MemorySpace(geometryModelMatrixCount)

            // Copy all vertices from geometry into vertex buffer:
            println("Geometry ${it.name} uploads ${it.points.size} vertices")
            for (point in it.points) {
                vertexBuffer.appendVertex(point, it.color, geometryModelMatrixCount)
            }

            // Copy indices into index buffer:
            indexBuffer.recordGeometry(it)

            // Increment count of global model matrices, since this geometry used one:
            geometryModelMatrixCount++

            // We increase the offset by the count of matrix slots consumed per geometry:
            modelMatrixOffset += Geometry.LOCAL_MATRICES_PER_GEOMETRY

            modelCount++

        }

        indexBuffer.endRecording()
    }

    override fun toString() = let {
        val sb = StringBuilder()
        sb.append("Geometry buffer with $modelCount of $maxModels models\n")

        sb.append("$geometryModelMatrixCount of $maxModels geometry model matrices:\n")

        for (i in 0 until maxModels) {
            sb.append("  Global [$i] #$i: ").append(modelMatrixMemory.toString(i)).append('\n')
        }

        sb.append("$modelCount * ${Geometry.LOCAL_MATRICES_PER_GEOMETRY} = " +
                "${modelCount * Geometry.LOCAL_MATRICES_PER_GEOMETRY} local model matrices\n")

        for (m in 0 until maxModels) {
            sb.append("  Global [$m] " +
                    "#${maxModels + m * Geometry.LOCAL_MATRICES_PER_GEOMETRY}:     ")

            sb.append(modelMatrixMemory.toString(maxModels + m * Geometry.LOCAL_MATRICES_PER_GEOMETRY)).append('\n')

            for (i in 0 until Geometry.LOCAL_MATRICES_PER_GEOMETRY) {
                sb.append("    Local [$m][$i] " +
                        "#${maxModels + m * Geometry.LOCAL_MATRICES_PER_GEOMETRY + 1 + i}: ")

                sb.append(modelMatrixMemory.toString(maxModels + m * Geometry.LOCAL_MATRICES_PER_GEOMETRY + 1 + i)).append('\n')
            }
        }

        sb.toString()
    }

}
