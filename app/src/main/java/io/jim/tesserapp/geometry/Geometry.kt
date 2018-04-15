package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.util.ListenerList
import junit.framework.Assert.assertTrue

/**
 * A geometrical structure consisting of vertices.
 */
open class Geometry(

        /**
         * This geometry's name.
         */
        val name: String,

        /**
         * Color of this geometry.
         */
        val color: Color = Color.BLACK

) {

    /**
     * Reference to model matrix buffer.
     */
    var buffer: MatrixBuffer? = null

    /**
     * Offset of memory section belonging to this particular geometry within the matrix buffer.
     *
     * A value of -1 indicates that this geometry is not attached to the root object and therefore
     * does not own a registered model matrix.
     *
     * Local and temporary matrices like translation are guaranteed to occupy the same, contiguous
     * section of memory. Therefore we don't need to store offsets for each individual matrix but
     * rather add offset constants to a single base offset to reach individual matrices.
     */
    var matrixOffset = -1

    /**
     * Offset of global model matrix.
     * A value of -1 indicates that this geometry is not attached to the root object and therefore
     * does not own a registered global model matrix.
     *
     * Unlike all the other matrices, the global matrix need not to lie in the same contiguous
     * memory section within the matrix buffer.
     */
    var matrixGlobal = -1

    /**
     * List of points.
     */
    val points = ArrayList<Vector>()

    /**
     * List of lines, represented by indices to two points.
     */
    val lines = ArrayList<Pair<Int, Int>>()

    private val children = ArrayList<Geometry>()
    private var parent: Geometry? = null
    private val matrixLocal get() = matrixOffset + Geometry.LOCAL_MATRIX
    private val matrixRotation get() = matrixOffset + Geometry.ROTATION_MATRIX
    private val matrixRotationZX get() = matrixOffset + Geometry.ROTATION_ZX_MATRIX
    private val matrixRotationYX get() = matrixOffset + Geometry.ROTATION_YX_MATRIX
    private val matrixTranslation get() = matrixOffset + Geometry.TRANSLATION_MATRIX

    companion object {

        /**
         * Count of matrices needed for one geometry.
         * This does not take the global matrix into account, since geometry buffers might
         * store them in a different memory section, see [io.jim.tesserapp.graphics.GeometryBuffer].
         */
        const val LOCAL_MATRICES_PER_GEOMETRY = 5

        private const val LOCAL_MATRIX = 0
        private const val ROTATION_MATRIX = 1
        private const val ROTATION_ZX_MATRIX = 2
        private const val ROTATION_YX_MATRIX = 3
        private const val TRANSLATION_MATRIX = 4

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
         * Listeners are fired every time a single point or line is added.
         */
        val onGeometryChangedListeners = ListenerList()
    }

    /**
     * Add a series of points.
     * The actual lines are drawn from indices to these points.
     */
    fun addPoints(vararg p: Vector) {
        synchronized(Geometry) {
            points += p
            onGeometryChangedListeners.fire()
        }
    }

    /**
     * Add a lines from point [a] to point [b].
     */
    fun addLine(a: Int, b: Int) {
        synchronized(Geometry) {
            lines += Pair(a, b)
            onGeometryChangedListeners.fire()
        }
    }

    /**
     * Remove all geometry data.
     */
    fun clearPoints() {
        synchronized(Geometry) {
            points.clear()
            lines.clear()
            onGeometryChangedListeners.fire()
        }
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate
     * counterparts.
     */
    fun extrude(direction: Vector) {
        synchronized(Geometry) {
            val size = points.size
            points += points.map { it + direction }
            lines += lines.map { Pair(it.first + size, it.second + size) }
            for (i in 0 until size) {
                addLine(i, i + size)
            }
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
        assertTrue("Geometry must be registered to a matrix buffer", buffer != null)

        // Rotation:
        buffer?.MemorySpace()?.multiply(matrixRotationZX, matrixRotationYX, matrixRotation)

        // Local:
        buffer?.MemorySpace()?.multiply(matrixRotation, matrixTranslation, matrixLocal)

        // Global:
        if (null != parent) {
            // This geometry has a parent, therefore we need to multiply the local matrix
            // to the parent's global matrix:
            buffer?.MemorySpace()?.multiply(matrixLocal, parent!!.matrixGlobal, matrixGlobal)
        }
        else {
            // This geometry has no parent, therefore the local matrix equals the global one:
            buffer?.MemorySpace()?.copy(matrixGlobal, matrixLocal)
        }

        children.forEach { it.computeModelMatricesRecursively() }
    }

    /**
     * Rotate in the zx plane around [theta].
     */
    fun rotationZX(theta: Double) {
        assertTrue("Geometry must be registered to a matrix buffer", buffer != null)
        buffer?.MemorySpace()?.rotation(matrixRotationZX, 2, 0, theta)
        onMatrixChangedListeners.fire()
    }

    /**
     * Rotate in the yx plane around [theta].
     */
    fun rotationYX(theta: Double) {
        assertTrue("Geometry must be registered to a matrix buffer", buffer != null)
        buffer?.MemorySpace()?.rotation(matrixRotationYX, 1, 0, theta)
        onMatrixChangedListeners.fire()
    }

    /**
     * Translate by [v].
     */
    fun translate(v: Vector) {
        assertTrue("Geometry must be registered to a matrix buffer", buffer != null)
        buffer?.MemorySpace()?.translation(matrixTranslation, v)
        onMatrixChangedListeners.fire()
    }

    /**
     * Add to a parent [parentGeometry].
     * This does not change the local transform, but rather the global one.
     */
    fun addToParentGeometry(parentGeometry: Geometry) {
        if (parent == parentGeometry) return

        // Release from former parent:
        parent?.children?.remove(this)

        // Re-parent to new geometry:
        parent = parentGeometry
        parentGeometry.children.add(this)

        // Fire children changed listener recursively:
        onHierarchyChangedListeners.fire()
    }

    /**
     * Release from its parent geometry.
     * This does not change the local transform, but rather the global one.
     */
    fun releaseFromParentGeometry() {
        parent?.children?.remove(this)
        parent = null

        // Fire children changed listener recursively:
        onHierarchyChangedListeners.fire()
    }

    /**
     * Invoke [f] for each geometry, recursively.
     */
    fun forEachRecursive(f: (Geometry) -> Unit) {
        f(this)
        children.forEach { child ->
            child.forEachRecursive(f)
        }
    }

    override fun toString(): String {
        return if (buffer != null) "$name: ${buffer!!.MemorySpace().toString(LOCAL_MATRIX)}" else name
    }

    /**
     * Return a string representation, including all children.
     */
    @Suppress("unused")
    fun toStringRecursive() = StringBuilder().let { sb ->
        toStringRecursive(0, sb)
        sb.toString()
    }

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
