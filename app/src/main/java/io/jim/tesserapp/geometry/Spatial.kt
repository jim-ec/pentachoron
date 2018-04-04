package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector

/**
 * A spatial object with no geometry but just transformation and child spatial data.
 */
open class Spatial : Iterable<Spatial> {

    private val global = Matrix()
    private val local = Matrix()
    private val rotation = Matrix()

    private val rotationZX = Matrix()
    private val rotationYX = Matrix()
    private val translation = Matrix()

    private val children = ArrayList<Spatial>()
    private var parent: Spatial? = null

    companion object {

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
     * The global transform.
     */
    fun modelMatrix(): Matrix {
        return if (null != parent) global else local
    }

    private fun computeLocal() {
        rotation.multiplicationFrom(rotationZX, rotationYX)
        local.multiplicationFrom(rotation, translation)

        if (null != parent) {
            global.multiplicationFrom(local, parent!!.modelMatrix())
        }

        children.forEach(Spatial::computeLocal)
    }

    /**
     * Rotate the spatial in the zx plane around [theta].
     */
    fun rotationZX(theta: Double) {
        rotationZX.rotation(2, 0, theta)
        computeLocal()
        onMatrixChangedListeners.forEach { it() }
    }

    /**
     * Rotate the spatial in the yx plane around [theta].
     */
    fun rotationYX(theta: Double) {
        rotationYX.rotation(1, 0, theta)
        computeLocal()
        onMatrixChangedListeners.forEach { it() }
    }

    /**
     * Translate the spatial by [v].
     */
    fun translate(v: Vector) {
        translation.translation(v)
        computeLocal()
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
        computeLocal()

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
        this.forEach { child ->
            f(child)
            child.forEachRecursive(f)
        }
    }

}
