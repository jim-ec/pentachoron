package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.util.ListenerList

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
    val vertexBuffer = FillUpBuffer(maxVertices,
            FillUpBuffer.Layout(
                    COMPONENTS_PER_POSITION,
                    COMPONENTS_PER_COLOR,
                    COMPONENTS_PER_MODEL_INDEX))

    /**
     * Root geometry of this manager.
     * To register hierarchical structured geometries, add them to this root.
     */
    val rootGeometry = Geometry("Root")

    /**
     * Listeners are fired when vertex data was changed in buffer.
     */
    val onVertexBufferUpdated = ListenerList()

    /**
     * Listeners are fired when model matrix data was changed in buffer.
     */
    val onModelMatrixBufferUpdated = ListenerList()

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
                //geometries.remove(removedGeometry)
                TODO("Implement geometry removal ($removedGeometry removed)")
                //modelMatrixBuffer -= removedGeometry
            }

            uploadVertexData()
        }

        Geometry.onGeometryChangedListeners += ::uploadVertexData
        Geometry.onMatrixChangedListeners += ::uploadModelMatrixData
    }

    private fun uploadVertexData() {
        // Rewrite vertex buffer:
        println("Upload vertex data")
        vertexBuffer.rewind()
        rootGeometry.forEachRecursive { geometry ->
            geometry.vertexPoints.forEach {
                vertexBuffer += listOf(
                        it.x.toFloat(), it.y.toFloat(), it.z.toFloat(),
                        geometry.color.red, geometry.color.green, geometry.color.blue,
                        geometry.modelIndex.toFloat())
            }
        }

        onVertexBufferUpdated.fire()
    }

    private fun uploadModelMatrixData() {
        rootGeometry.computeModelMatricesRecursively()
        onModelMatrixBufferUpdated.fire()
    }

}
