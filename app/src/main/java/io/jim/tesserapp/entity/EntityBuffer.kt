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
    val root = Entity(matrixBuffer, 0, globalMatrixCount)

    /**
     * Map of each name to its entity.
     */
    private val entities = mapOf(

            // Add the default root item:
            Pair("Root", root),

            // Add all the names to newly created entities:
            *(Array(names.size) { index ->
                Pair(
                        // The name of that entity:
                        names[index],

                        // Create one parented entity for each given name:
                        Entity(
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
                )
            })
    )

    init {
        Entity.addHierarchyChangedListener(::computeModelMatrices)
        Entity.addMatrixChangedListener(::computeModelMatrices)
    }

    /**
     * Return the entity named [name].
     * @throws NoSuchEntityException If no entity named [name] exist.
     */
    operator fun get(name: String): Entity = entities[name] ?: throw NoSuchEntityException(name)

    /**
     * Re-computes model matrices.
     */
    private fun computeModelMatrices() {
        root.computeModelMatrixRecursively(null)
    }

}
