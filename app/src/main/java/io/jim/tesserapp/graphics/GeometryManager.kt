package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry

/**
 * Manages a geometry tree, while providing backing buffers for vertex and matrix data.
 *
 * This geometry buffer is only responsible for raw data, without incorporating with OpenGL at all.
 */
class GeometryManager(maxGeometries: Int, maxVertices: Int) {

    val modelMatrixBuffer = ModelMatrixBuffer(maxGeometries)
    val vertexBuffer = FillUpBuffer(maxVertices, FillUpBuffer.Layout(COMPONENTS_PER_POSITION, COMPONENTS_PER_COLOR, COMPONENTS_PER_MODEL_INDEX))
    val rootGeometry = Geometry("Root")
    private val geometries = HashSet<Geometry>()

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
                TODO("Implement geometry removal ($removedGeometry removed)")
                //modelMatrixBuffer -= removedGeometry
            }
        }

        Geometry.onGeometryChangedListeners += {

            // Rewrite vertex buffer:
            vertexBuffer.rewind()
            rootGeometry.forEachRecursive { geometry ->
                geometry.vertexPoints.forEach {
                    vertexBuffer += listOf(
                            it.x.toFloat(), it.y.toFloat(), it.z.toFloat(),
                            geometry.color.red, geometry.color.green, geometry.color.blue,
                            geometry.modelIndex.toFloat())
                }
            }
        }

        Geometry.onMatrixChangedListeners += {
            rootGeometry.computeModelMatricesRecursively()
        }
    }

}
