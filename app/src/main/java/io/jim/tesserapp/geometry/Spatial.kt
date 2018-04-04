package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector

/**
 * A spatial object with no geometry but just transformation and child spatial data.
 */
open class Spatial(

        /**
         * This spatial's name.
         */
        val name: String

) : Iterable<Spatial> {

    /**
     * Reference to model matrix buffer.
     */
    lateinit var buffer: Array<Matrix>

    /**
     * Offset of model matrix.
     * A value of -1 indicates that this spatial is not attached to the root object and therefore
     * does not own a registered model matrix.
     */
    var offset = -1

    /**
     * Offset of global model matrix.
     * A value of -1 indicates that this spatial is not attached to the root object and therefore
     * does not own a registered global model matrix.
     */
    var globalModelMatrixOffset = -1

    private val children = ArrayList<Spatial>()
    private var parent: Spatial? = null

    companion object {

        /**
         * Count of matrices needed for one spatial.
         */
        const val MATRICES_PER_SPATIAL = 5

        private const val LOCAL_MATRIX = 0
        private const val ROTATION_MATRIX = 1
        private const val ROTATION_ZX_MATRIX = 2
        private const val ROTATION_YX_MATRIX = 3
        private const val TRANSLATION_MATRIX = 4

        private val onMatrixChangedListeners = ArrayList<() -> Unit>()
        private val onChildrenChangedListeners = ArrayList<() -> Unit>()

        /**
         * Add a listener to when the transform data is changed.
         */
        fun addMatrixChangedListener(f: () -> Unit) {
            onMatrixChangedListeners.add(f)
        }

        /**
         * Add a listener to when the hierarchical parent-children data is changed.
         */
        fun addChildrenChangedListener(f: () -> Unit) {
            onChildrenChangedListeners.add(f)
        }

    }

    /**
     * Becomes true if model matrices needs to be re-calculated.
     */
    var rebuildModelMatrices = false
        private set

    /**
     * Recomputes all model matrices recursively.
     */
    fun computeLocal() {
        if (!rebuildModelMatrices) return

        // Rotation:
        buffer[offset + ROTATION_MATRIX].multiplicationFrom(buffer[offset + ROTATION_ZX_MATRIX], buffer[offset + ROTATION_YX_MATRIX])

        // Local:
        buffer[offset + LOCAL_MATRIX].multiplicationFrom(buffer[offset + ROTATION_MATRIX], buffer[offset + TRANSLATION_MATRIX])

        // Global:
        if (null != parent) {
            buffer[globalModelMatrixOffset].multiplicationFrom(
                    buffer[offset + LOCAL_MATRIX],
                    parent!!.buffer[parent!!.globalModelMatrixOffset])
        }
        else {
            buffer[globalModelMatrixOffset] = buffer[offset + LOCAL_MATRIX].copy()
        }

        rebuildModelMatrices = true
        children.forEach { spatial -> spatial.computeLocal() }
    }

    private fun requestRebuildModelMatrices() {
        rebuildModelMatrices = true
        parent?.requestRebuildModelMatrices()
    }

    /**
     * Rotate the spatial in the zx plane around [theta].
     */
    fun rotationZX(theta: Double) {
        buffer[offset + ROTATION_ZX_MATRIX].rotation(2, 0, theta)
        requestRebuildModelMatrices()
        onMatrixChangedListeners.forEach { it() }
    }

    /**
     * Rotate the spatial in the yx plane around [theta].
     */
    fun rotationYX(theta: Double) {
        buffer[offset + ROTATION_YX_MATRIX].rotation(1, 0, theta)
        requestRebuildModelMatrices()
        onMatrixChangedListeners.forEach { it() }
    }

    /**
     * Translate the spatial by [v].
     */
    fun translate(v: Vector) {
        buffer[offset + TRANSLATION_MATRIX].translation(v)
        requestRebuildModelMatrices()
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

        // Re-compute global transform:
        requestRebuildModelMatrices()

        // Fire children changed listener recursively:
        onChildrenChangedListeners.forEach { it() }
    }

    /**
     * Release this spatial from its parent spatial.
     * This does not change the local transform, but rather the global one.
     */
    fun releaseFromParentSpatial() {
        parent?.children?.remove(this)
        parent = null

        // Fire children changed listener recursively:
        onChildrenChangedListeners.forEach { it() }
    }

    override fun iterator() = children.iterator()

    /**
     * Invoke [f] for each spatial, recursively.
     */
    fun forEachRecursive(f: (Spatial) -> Unit) {
        f(this)
        this.forEach { child ->
            child.forEachRecursive(f)
        }
    }

    override fun toString(): String {
        return "$name: ${buffer[LOCAL_MATRIX]}"
    }

}
