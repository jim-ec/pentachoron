package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.util.RandomAccessBuffer

/**
 * Manages a geometry tree, while providing backing buffers for vertex and matrix data.
 *
 * This geometry buffer is only responsible for raw data, without incorporating with OpenGL at all.
 */
class GeometryManager(maxGeometries: Int) {

    /**
     * Model matrix buffer.
     * When geometries are transformed, this buffer is updated automatically.
     */
    val modelMatrixBuffer = ModelMatrixBuffer(maxGeometries)

    /**
     * Vertex buffer.
     * Buffer data is updated automatically upon geometrical change.
     */
    val vertexBuffer = RandomAccessBuffer<Vertex>(100, Vertex.COMPONENTS_PER_VERTEX)

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
        var globalVertexIndex = 0
        rootGeometry.forEachRecursive { geometry ->
            geometry.vertices.also { vertices ->
                vertices.forEachIndexed { localVertexIndex, vertex ->
                    vertexBuffer[globalVertexIndex + localVertexIndex] = vertex
                }
                globalVertexIndex += vertices.size
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
