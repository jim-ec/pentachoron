package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry

/**
 * Manages a geometry tree, while providing backing buffers for vertex and matrix data.
 *
 * This geometry buffer is only responsible for raw data, without incorporating with OpenGL at all.
 */
class GeometryManager(maxGeometries: Int, maxVertices: Int) {

    /**
     * Model matrix buffer.
     * When geometries are transformed, this buffer is updated automatically.
     */
    val modelMatrixBuffer = ModelMatrixBuffer(maxGeometries)

    /**
     * Vertex buffer.
     * Buffer data is updated automatically upon geometrical change.
     */
    val vertexBuffer = FloatLayoutBuffer<Vertex>(maxVertices,
            FloatLayoutBuffer.Layout(
                    COMPONENTS_PER_POSITION,
                    COMPONENTS_PER_COLOR,
                    COMPONENTS_PER_MODEL_INDEX))

    /**
     * Root geometry of this manager.
     * To register hierarchical structured geometries, add them to this root.
     */
    val rootGeometry = Geometry("Root")

    /**
     * Set to false if vertex buffer was re-written and needs to be uploaded to the GPU.
     */
    var verticesUpdated = true

    private var geometries = HashSet<Geometry>()

    companion object {
        internal const val COMPONENTS_PER_POSITION = 3
        internal const val COMPONENTS_PER_COLOR = 3
        internal const val COMPONENTS_PER_MODEL_INDEX = 1
    }

    init {
        Geometry.onHierarchyChangedListeners += {

            // Get a set of all currently added geometries:
            val currentGeometries = HashSet<Geometry>().apply {
                rootGeometry.forEachRecursive { add(it) }
            }

            // Get a set of all newly added geometries:
            HashSet<Geometry>().apply {
                addAll(currentGeometries)
                removeAll(geometries)
            }.forEach { newGeometry ->
                geometries.add(newGeometry)
                modelMatrixBuffer += newGeometry
            }

            // Get a set of all removed geometries:
            HashSet<Geometry>().apply {
                addAll(geometries)
                removeAll(currentGeometries)
            }.forEach { removedGeometry ->
                geometries.remove(removedGeometry)
                modelMatrixBuffer -= removedGeometry
            }

            uploadVertexData()
            computeModelMatrices()
        }

        Geometry.onGeometryChangedListeners += ::uploadVertexData
    }

    private fun uploadVertexData() {
        // Rewrite vertex buffer:
        vertexBuffer.rewind()
        rootGeometry.forEachRecursive { geometry ->
            geometry.vertices.forEach {
                vertexBuffer += it
            }
        }
        verticesUpdated = true
    }

    /**
     * Recomputes model matrices.
     */
    fun computeModelMatrices() {
        rootGeometry.computeModelMatricesRecursively()
    }

}
