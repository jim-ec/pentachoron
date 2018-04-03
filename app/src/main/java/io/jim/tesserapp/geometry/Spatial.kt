package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.Matrix
import io.jim.tesserapp.math.Vector

open class Spatial(
        val dimension: Int,
        private val onChildrenChangedListener: (() -> Unit)? = null,
        var onMatrixChangedListener: (() -> Unit)? = null
) : Iterable<Spatial> {

    private val global = Matrix(dimension)
    private val local = Matrix(dimension)
    private val rotation = Matrix(dimension)

    private val rotationZX = Matrix(dimension)
    private val rotationYX = Matrix(dimension)
    private val translation = Matrix(dimension)

    private val children = ArrayList<Spatial>()
    private var parent: Spatial? = null

    fun modelMatrix(): Matrix {
        rotation.multiplicationFrom(rotationZX, rotationYX)
        local.multiplicationFrom(rotation, translation)
        return if (null != parent) global.multiplicationFrom(local, parent!!.modelMatrix())
        else local
    }

    fun rotationZX(theta: Double) {
        rotationZX.rotation(2, 0, theta)
        invokeOnMatrixChangedListenerRecursive()
    }

    fun rotationYX(theta: Double) {
        rotationYX.rotation(1, 0, theta)
        invokeOnMatrixChangedListenerRecursive()
    }

    fun translate(v: Vector) {
        translation.translation(v)
        invokeOnMatrixChangedListenerRecursive()
    }

    fun addChildSpatial(spatial: Spatial) {
        children.add(spatial)
        spatial.parent = this
        invokeOnChildrenChangedListenerRecursive()
    }

    override fun iterator() = children.iterator()

    private fun invokeOnChildrenChangedListenerRecursive() {
        onChildrenChangedListener?.let { it() }
        parent?.invokeOnChildrenChangedListenerRecursive()
    }

    private fun invokeOnMatrixChangedListenerRecursive() {
        onMatrixChangedListener?.let { it() }
        parent?.invokeOnMatrixChangedListenerRecursive()
    }

    fun forEachRecursive(f: (Spatial) -> Unit) {
        this.forEach { child ->
            f(child)
            child.forEachRecursive(f)
        }
    }

}
