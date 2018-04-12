package io.jim.tesserapp.entity

import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertTrue

/**
 * Represents a transformable item in a hierarchy tree.
 *
 * Drawable geometry does not subclass from this class, but rather is contained by this class
 * as an aspect.
 *
 * Entities are not intended to be constructed by the user, but instead by the [EntityBuffer].
 */
data class Entity internal constructor(

        /**
         * Reference to model matrix buffer.
         */
        private val matrixBuffer: MatrixBuffer,

        /**
         * Offset of global model matrix.
         *
         * Unlike all the other matrices, the global matrix need not to lie in the same
         * contiguous memory section within the matrix buffer. This is because all the global
         * model matrices belonging to actually drawn geometry is kept in a separate memory
         * section, optimized for fast uniform array upload.
         */
        private val matrixGlobal: Int,

        /**
         * Offset of memory section belonging to this particular spatial within the matrix
         * buffer.
         *
         * Local and temporary matrices like translation are guaranteed to occupy the same,
         * contiguous section of memory. Therefore we don't need to store offsets for each
         * individual matrix but rather add offset constants to a single base offset to reach
         * individual matrices.
         */
        private val matrixOffset: Int

) : Iterable<Entity> {


    /**
     * Models belonging to this entity.
     */
    val models = ArrayList<Model>()

    private val children = ArrayList<Entity>()
    private val matrixLocal get() = matrixOffset + LOCAL_MATRIX
    private val matrixRotation get() = matrixOffset + ROTATION_MATRIX
    private val matrixRotationZX get() = matrixOffset + ROTATION_ZX_MATRIX
    private val matrixRotationYX get() = matrixOffset + ROTATION_YX_MATRIX
    private val matrixTranslation get() = matrixOffset + TRANSLATION_MATRIX

    companion object {

        /**
         * Count of matrices needed for one spatial.
         * This does not take the global matrix into account, since geometry buffers might
         * store them in a different memory section, see [EntityBuffer].
         */
        const val LOCAL_MATRICES_PER_SPATIAL = 5

        internal const val LOCAL_MATRIX = 0
        internal const val ROTATION_MATRIX = 1
        internal const val ROTATION_ZX_MATRIX = 2
        internal const val ROTATION_YX_MATRIX = 3
        internal const val TRANSLATION_MATRIX = 4

        /**
         * Added listeners are fired when hierarchical structure, i.e. parent-child relationships,
         * changed.
         */
        val onHierarchyChangedListeners = ListenerList()

        /**
         * Added listeners are fired when matrix data, i.e. transform, of one entity changed.
         */
        val onMatrixChangedListeners = ListenerList()

        /**
         * Added listeners are fired when model data of one entity changed.
         */
        val onModelChangedListeners = ListenerList()

    }

    /**
     * Add [children] to this entity.
     */
    fun addChildren(vararg children: Entity) {
        assertTrue("Children must have same matrix buffer", children.all { matrixBuffer == it.matrixBuffer })
        this.children += children
        onHierarchyChangedListeners.fire()
    }

    /**
     * Remove a [child] from this entity.
     */
    fun removeChild(child: Entity) {
        assertTrue("Children must have same matrix buffer", matrixBuffer == child.matrixBuffer)
        if (!children.remove(child)) throw NoSuchEntityException()
        onHierarchyChangedListeners.fire()
    }

    /**
     * Return an iterator to this entity's children.
     */
    override operator fun iterator() = children.iterator()

    /**
     * Compute global model matrices.
     */
    fun computeModelMatrixRecursively(parentGlobalMatrix: Int?) {

        // Rotation:
        matrixBuffer.multiply(matrixRotationZX, matrixRotationYX, matrixRotation)

        // Local:
        matrixBuffer.multiply(matrixRotation, matrixTranslation, matrixLocal)

        // Global:
        if (null != parentGlobalMatrix) {
            // This spatial has a parent, therefore we need to multiply the local matrix
            // to the parent's global matrix:
            matrixBuffer.multiply(matrixLocal, parentGlobalMatrix, matrixGlobal)
        }
        else {
            // This spatial has no parent, therefore the local matrix equals the global one:
            matrixBuffer.copy(matrixGlobal, matrixLocal)
        }

        // Compute children model matrices:
        children.forEach { child -> child.computeModelMatrixRecursively(matrixGlobal) }
    }

    /**
     * Rotate the spatial in the zx plane around [theta].
     */
    fun rotationZX(theta: Double) {
        matrixBuffer.rotation(matrixRotationZX, 2, 0, theta)
        onMatrixChangedListeners.fire()
    }

    /**
     * Rotate the spatial in the yx plane around [theta].
     */
    fun rotationYX(theta: Double) {
        matrixBuffer.rotation(matrixRotationYX, 1, 0, theta)
        onMatrixChangedListeners.fire()
    }

    /**
     * Translate the spatial by [v].
     */
    fun translation(v: Vector) {
        matrixBuffer.translation(matrixTranslation, v)
        onMatrixChangedListeners.fire()
    }

}

/**
 * Thrown when trying to access a non-existent entity.
 */
class NoSuchEntityException(name: String? = null) : Exception(if (null != name) "No such entity: $name" else "No such entity")
