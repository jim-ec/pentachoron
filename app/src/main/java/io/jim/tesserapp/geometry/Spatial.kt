package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector

open class Spatial(val dimension: Int) : Iterable<Spatial> {

    private val global = Matrix(dimension)
    private val local = Matrix(dimension)
    private val rotation = Matrix(dimension)

    private val rotationZX = Matrix(dimension)
    private val rotationYX = Matrix(dimension)
    private val translation = Matrix(dimension)

    private val children = ArrayList<Spatial>()
    private var parent: Spatial? = null

    companion object {

        private val onMatrixChangedListeners = ArrayList<() -> Unit>()
        private val onChildrenChangedListeners = ArrayList<() -> Unit>()

        fun addMatrixChangedListener(f: () -> Unit) {
            onMatrixChangedListeners.add(f)
        }

        fun addChildrenChangedListener(f: () -> Unit) {
            onChildrenChangedListeners.add(f)
        }

    }

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

    fun rotationZX(theta: Double) {
        rotationZX.rotation(2, 0, theta)
        computeLocal()
        onMatrixChangedListeners.forEach { it() }
    }

    fun rotationYX(theta: Double) {
        rotationYX.rotation(1, 0, theta)
        computeLocal()
        onMatrixChangedListeners.forEach { it() }
    }

    fun translate(v: Vector) {
        translation.translation(v)
        computeLocal()
        onMatrixChangedListeners.forEach { it() }
    }

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

    fun releaseFromParentSpatial() {
        parent?.children?.remove(this)
        parent = null

        // Fire children changed listener recursively:
        onChildrenChangedListeners.forEach { it() }
    }

    override fun iterator() = children.iterator()

    fun forEachRecursive(f: (Spatial) -> Unit) {
        this.forEach { child ->
            f(child)
            child.forEachRecursive(f)
        }
    }

}
