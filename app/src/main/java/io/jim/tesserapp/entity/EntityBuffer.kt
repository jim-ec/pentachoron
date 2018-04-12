package io.jim.tesserapp.entity

import io.jim.tesserapp.math.MatrixBuffer

/**
 * Hold entities of constant count. The instances itself cannot change.
 * Transform and geometry data, as well as parent-child relation ships are dynamic.
 */
@Suppress("unused")
class EntityBuffer(vararg names: String) {

    private val globalMatrixCount = names.size + 1

    /**
     * Matrix buffer provided for the entities to store their matrices in.
     */
    private val matrixBuffer = MatrixBuffer(
            globalMatrixCount,                                        // Global matrices
            globalMatrixCount * Entity.LOCAL_MATRICES_PER_SPATIAL     // Local matrices
    )

    /**
     * The root entity.
     * All entities are initially parented to this entity.
     */
    val root = Entity("Root", matrixBuffer, 0, globalMatrixCount)

    /**
     * Map of each name to its entity.
     */
    private val entities = arrayOf(

            // Add the default root item:
            root,

            // Add all the names to newly created entities:
            *(Array(names.size) { index ->
                Entity(
                        // The name of that entity:
                        names[index],

                        // Create one parented entity for each given name:
                        // All entities are bound to this' geometry buffer:
                        matrixBuffer,

                        // Compute offset for local matrices:
                        globalMatrixCount + index * Entity.LOCAL_MATRICES_PER_SPATIAL,

                        // Compute offset for global matrix:
                        index + 1
                ).also {

                    // And add it to the root:
                    root.addChildren(it)
                }
            })
    )

    init {
        Entity.onHierarchyChangedListeners += ::computeModelMatrices
        Entity.onMatrixChangedListeners += ::computeModelMatrices
    }

    /**
     * Thrown when trying to access a non-existent entity.
     */
    class NoSuchEntityException(name: String) : Exception("No such entity: $name")

    /**
     * Return the entity named [name].
     * @throws NoSuchEntityException If no entity named [name] exist.
     */
    operator fun get(name: String): Entity = entities.find { name == it.name }
            ?: throw NoSuchEntityException(name)

    /**
     * Re-computes model matrices.
     */
    private fun computeModelMatrices() {
        root.computeModelMatrixRecursively(null)
    }

}
