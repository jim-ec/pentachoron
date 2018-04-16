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

    private var geometries = HashSet<Geometry>()

    val onVertexBufferUpdated = ListenerList()
    val onModelMatrixBufferUpdated = ListenerList()

    companion object {
        internal const val COMPONENTS_PER_POSITION = 3
        internal const val COMPONENTS_PER_COLOR = 3
        internal const val COMPONENTS_PER_MODEL_INDEX = 1
    }

    init {
        Geometry.onHierarchyChangedListeners += {

            println("Hierarchy changed:")

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
                println("    $newGeometry added")
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

            //geometries = currentGeometries
            println("finished with hierarchy change, upload geometry:")

            uploadVertexData()
        }

        Geometry.onGeometryChangedListeners += ::uploadVertexData
        Geometry.onMatrixChangedListeners += ::uploadModelMatrixData
    }

    private fun uploadVertexData() {
        println("Geometry changed:")

        // Rewrite vertex buffer:
        vertexBuffer.rewind()
        rootGeometry.forEachRecursive { geometry ->
            println("    $geometry uploads ${geometry.vertexPoints.size} vertices")
            geometry.vertexPoints.forEach {
                vertexBuffer += listOf(
                        it.x.toFloat(), it.y.toFloat(), it.z.toFloat(),
                        geometry.color.red, geometry.color.green, geometry.color.blue,
                        geometry.modelIndex.toFloat())
            }
        }

        onVertexBufferUpdated.fire()

        println("finished with geometry change")
    }

    private fun uploadModelMatrixData() {
        //println("Recompute model matrices")
        rootGeometry.computeModelMatricesRecursively()
        onModelMatrixBufferUpdated.fire()
        //println("finished with model matrix change")
    }

}
