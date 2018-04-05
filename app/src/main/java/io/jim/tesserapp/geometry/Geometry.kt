package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.util.ListenerList
import kotlin.math.max

/**
 * A geometrical structure consisting of vertices.
 *
 * Matrix data is not stored in this class, but instead, each geometry gets memory spaces, providing
 * reserved memory section into a large matrix buffer to store matrix data.
 *
 * This implies that unless the geometry is registered into such a matrix buffer, you cannot
 * transform or even query transformation at all.
 */
open class Geometry(

        /**
         * This geometry's name.
         */
        val name: String,

        /**
         * Color of this geometry.
         */
        val baseColor: Color = Color.BLACK

) {

    /**
     * Memory section this geometry can store its global matrix.
     */
    var globalMemory: MatrixBuffer.MemorySpace? = null

    private val localMemory = MatrixBuffer(LOCAL_MATRICES_PER_GEOMETRY).MemorySpace()

    /**
     * Thrown when trying to transform the geometry while not registered into a matrix buffer.
     */
    inner class NotRegisteredIntoMatrixBufferException
        : Exception("Geometry not registered into matrix buffer")

    /**
     * List of vertices.
     */
    val vertices = ArrayList<Vertex>()

    /**
     * List of lines, represented by indices to two vertices.
     */
    val lines = ArrayList<Pair<Int, Int>>()

    /**
     * Model index of this geometry.
     * Is only knowable after is has been registered into a model matrix buffer.
     */
    val modelIndex
        get() = globalMemory?.offset ?: throw NotRegisteredIntoMatrixBufferException()

    /**
     * List of vertices, but with resolved indices.
     * This is intended to be used for drawing without indices.
     */
    val vertexPoints: List<Vertex>
        get() = let {
            lines.flatMap { (first, second) -> listOf(vertices[first], vertices[second]) }
        }

    private val children = ArrayList<Geometry>()
    private var parent: Geometry? = null

    companion object {

        private const val LOCAL_MATRICES_PER_GEOMETRY = 5
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

        /**
         * Vertex data is re-uploaded after the calling [f].
         *
         * Calls to [geometrical] can even be nested, and only after the outermost execution
         * finished, the vertex data gets uploaded.
         *
         * This is really useful to reduces vertex re-uploaded during many geometrical structure
         * changes at once.
         *
         * Calls to [geometrical] are cross-thread synchronized.
         */
        inline fun geometrical(f: () -> Unit) {
            synchronized(Geometry) {
                geometricalCounter++
                f()
                geometricalCounter--
                if (0 == geometricalCounter) {
                    onGeometryChangedListeners.fire()
                }
            }
        }

        /**
         * Every time this counter reaches 0, geometry change listeners are fired.
         * The counter can never be negative, and is incremented/decremented automatically
         * on calls to [geometrical].
         */
        @Suppress("MemberVisibilityCanBePrivate")
        var geometricalCounter = 0
            set(value) {
                field = max(value, 0)
            }

    }

    /**
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    protected fun addPoint(position: Vector, color: Color = baseColor) {
        geometrical {
            vertices += Vertex(position, color)
        }
    }

    /**
     * Add a lines from point [a] to point [b].
     */
    protected fun addLine(a: Int, b: Int) {
        geometrical {
            lines += Pair(a, b)
        }
    }

    /**
     * Remove all geometry data.
     */
    protected fun clearPoints() {
        geometrical {
            vertices.clear()
            lines.clear()
        }
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate
     * counterparts.
     */
    fun extrude(direction: Vector) {
        geometrical {
            val size = vertices.size
            vertices += vertices.map { Vertex(it.position + direction, it.color) }
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
        synchronized(Geometry) {

            val globalMemory = globalMemory ?: throw NotRegisteredIntoMatrixBufferException()

            // Rotation:
            localMemory.multiply(lhs = ROTATION_ZX_MATRIX, rhs = ROTATION_YX_MATRIX, matrix = ROTATION_MATRIX)

            // Local:
            localMemory.multiply(lhs = ROTATION_MATRIX, rhs = TRANSLATION_MATRIX, matrix = LOCAL_MATRIX)

            // Global:
            if (null != parent) {
                // This geometry has a parent, therefore we need to multiply the local matrix
                // to the parent's global matrix:
                globalMemory.multiply(
                        lhs = LOCAL_MATRIX, lhsMemorySpace = localMemory,
                        rhsMemorySpace = parent!!.globalMemory
                                ?: throw NotRegisteredIntoMatrixBufferException())
            }
            else {
                // This geometry has no parent, therefore the local matrix equals the global one:
                globalMemory.copy(source = LOCAL_MATRIX, sourceMemorySpace = localMemory)
            }

            children.forEach { it.computeModelMatricesRecursively() }
        }
    }

    /**
     * Rotate in the zx plane around [theta].
     */
    fun rotationZX(theta: Double) {
        synchronized(Geometry) {
            localMemory.rotation(ROTATION_ZX_MATRIX, 2, 0, theta)
            onMatrixChangedListeners.fire()
        }
    }

    /**
     * Rotate in the yx plane around [theta].
     */
    fun rotationYX(theta: Double) {
        synchronized(Geometry) {
            localMemory.rotation(ROTATION_YX_MATRIX, 1, 0, theta)
            onMatrixChangedListeners.fire()
        }
    }

    /**
     * Translate by [v].
     */
    fun translate(v: Vector) {
        synchronized(Geometry) {
            localMemory.translation(TRANSLATION_MATRIX, v)
            onMatrixChangedListeners.fire()
        }
    }

    /**
     * Add to a parent [parentGeometry].
     * This does not change the local transform, but rather the global one.
     */
    fun addToParentGeometry(parentGeometry: Geometry) {
        synchronized(Geometry) {
            if (parent == parentGeometry) return

            // Release from former parent:
            parent?.children?.remove(this)

            // Re-parent to new geometry:
            parent = parentGeometry
            parentGeometry.children.add(this)

            // Fire children changed listener recursively:
            onHierarchyChangedListeners.fire()
        }
    }

    /**
     * Release from its parent geometry.
     * This does not change the local transform, but rather the global one.
     */
    fun releaseFromParentGeometry() {
        synchronized(Geometry) {
            parent?.children?.remove(this)
            parent = null

            // Fire children changed listener recursively:
            onHierarchyChangedListeners.fire()
        }
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

    override fun toString() = name

    /**
     * Return a string representation, including all children.
     */
    @Suppress("unused")
    fun toStringRecursive() = let {
        val sb = StringBuilder()
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
