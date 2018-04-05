package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertTrue

/**
 * A spatial object with no geometry but just transformation and child spatial data.
 */
open class Spatial(

        /**
         * This spatial's name.
         */
        val name: String

) {

    /**
     * Reference to model matrix buffer.
     */
    var buffer: MatrixBuffer? = null

    /**
     * Offset of memory section belonging to this particular spatial within the matrix buffer.
     *
     * A value of -1 indicates that this spatial is not attached to the root object and therefore
     * does not own a registered model matrix.
     *
     * Local and temporary matrices like translation are guaranteed to occupy the same, contiguous
     * section of memory. Therefore we don't need to store offsets for each individual matrix but
     * rather add offset constants to a single base offset to reach individual matrices.
     */
    var matrixOffset = -1

    /**
     * Offset of global model matrix.
     * A value of -1 indicates that this spatial is not attached to the root object and therefore
     * does not own a registered global model matrix.
     *
     * Unlike all the other matrices, the global matrix need not to lie in the same contiguous
     * memory section within the matrix buffer. This is because all the global model matrices
     * belonging to actually drawn geometry is kept in a separate memory section, optimized
     * for fast uniform array upload.
     */
    var matrixGlobal = -1

    private val children = ArrayList<Spatial>()
    private var parent: Spatial? = null
    private val matrixLocal get() = matrixOffset + LOCAL_MATRIX
    private val matrixRotation get() = matrixOffset + ROTATION_MATRIX
    private val matrixRotationZX get() = matrixOffset + ROTATION_ZX_MATRIX
    private val matrixRotationYX get() = matrixOffset + ROTATION_YX_MATRIX
    private val matrixTranslation get() = matrixOffset + TRANSLATION_MATRIX

    companion object {

        /**
         * Count of matrices needed for one spatial.
         * This does not take the global matrix into account, since geometry buffers might
         * store them in a different memory section, see [io.jim.tesserapp.graphics.GeometryBuffer].
         */
        const val LOCAL_MATRICES_PER_SPATIAL = 5

        private const val LOCAL_MATRIX = 0
        private const val ROTATION_MATRIX = 1
        private const val ROTATION_ZX_MATRIX = 2
        private const val ROTATION_YX_MATRIX = 3
        private const val TRANSLATION_MATRIX = 4

        private val onMatrixChangedListeners = ArrayList<() -> Unit>()
        private val onHierarchyChangedListeners = ArrayList<() -> Unit>()

        /**
         * Listener [f] is fired when matrix data is changed.
         */
        fun addMatrixChangedListener(f: () -> Unit) {
            onMatrixChangedListeners.add(f)
        }

        /**
         * Listener [f] is fired when the hierarchical parent-children data is changed.
         */
        fun addHierarchyChangedListener(f: () -> Unit) {
            onHierarchyChangedListeners.add(f)
        }

    }

    /**
     * Recomputes all model matrices recursively.
     *
     * Since this recursive function computes model matrices for its children after it's done
     * with its one global matrix, it can safely access its parent's global model matrix,
     * since that is guaranteed to already be computed.
     */
    fun computeModelMatricesRecursively() {
        assertTrue("Spatial must be registered to a matrix buffer", buffer != null)

        // Rotation:
        buffer?.multiply(matrixRotationZX, matrixRotationYX, matrixRotation)

        // Local:
        buffer?.multiply(matrixRotation, matrixTranslation, matrixLocal)

        // Global:
        if (null != parent) {
            // This spatial has a parent, therefore we need to multiply the local matrix
            // to the parent's global matrix:
            buffer?.multiply(matrixLocal, parent!!.matrixGlobal, matrixGlobal)
        }
        else {
            // This spatial has no parent, therefore the local matrix equals the global one:
            buffer?.copy(matrixGlobal, matrixLocal)
        }

        children.forEach { spatial -> spatial.computeModelMatricesRecursively() }
    }

    /**
     * Rotate the spatial in the zx plane around [theta].
     */
    fun rotationZX(theta: Double) {
        assertTrue("Spatial must be registered to a matrix buffer", buffer != null)
        buffer?.rotation(matrixRotationZX, 2, 0, theta)
        onMatrixChangedListeners.forEach { it() }
    }

    /**
     * Rotate the spatial in the yx plane around [theta].
     */
    fun rotationYX(theta: Double) {
        assertTrue("Spatial must be registered to a matrix buffer", buffer != null)
        buffer?.rotation(matrixRotationYX, 1, 0, theta)
        onMatrixChangedListeners.forEach { it() }
    }

    /**
     * Translate the spatial by [v].
     */
    fun translate(v: Vector) {
        assertTrue("Spatial must be registered to a matrix buffer", buffer != null)
        buffer?.translation(matrixTranslation, v)
        onMatrixChangedListeners.forEach { it() }
    }

    /**
     * Add this spatial to a parent [spatial].
     * This does not change the local transform, but rather the global one.
     */
    fun addToParentSpatial(spatial: Spatial) {
        if (parent == spatial) return

        // Release from former parent:
        parent?.children?.remove(this)

        // Re-parent to new spatial:
        parent = spatial
        spatial.children.add(this)

        // Fire children changed listener recursively:
        onHierarchyChangedListeners.forEach { it() }
    }

    /**
     * Release this spatial from its parent spatial.
     * This does not change the local transform, but rather the global one.
     */
    fun releaseFromParentSpatial() {
        parent?.children?.remove(this)
        parent = null

        // Fire children changed listener recursively:
        onHierarchyChangedListeners.forEach { it() }
    }

    /**
     * Invoke [f] for each spatial, recursively.
     */
    fun forEachRecursive(f: (Spatial) -> Unit) {
        f(this)
        children.forEach { child ->
            child.forEachRecursive(f)
        }
    }

    override fun toString(): String {
        return if (buffer != null) "$name: ${buffer!!.toString(LOCAL_MATRIX)}" else name
    }

    /**
     * Return spatial in a string representation, including all children.
     */
    @Suppress("unused")
    fun toStringRecursive() = StringBuilder().also { sb ->
        toStringRecursive(0, sb)
    }.toString()

    private fun toStringRecursive(indent: Int, sb: StringBuilder) {
        for (i in 0 until indent) {
            sb.append(" -> ")
        }
        sb.append(this).append('\n')
        children.forEach {
            it.toStringRecursive(indent + 1, sb)
        }
    }

}
