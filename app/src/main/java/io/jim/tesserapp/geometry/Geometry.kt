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

    /**
     * Model index of this geometry.
     * Is only knowable after is has been registered into a model matrix buffer.
     */
    val modelIndex
        get() = globalMemory?.offset ?: throw NotRegisteredIntoMatrixBufferException()

    private val localMemory = MatrixBuffer(LOCAL_MATRICES_PER_GEOMETRY).MemorySpace()
    private val positions = ArrayList<Vector>()
    private val lines = ArrayList<LineIndices>()
    private val children = ArrayList<Geometry>()
    private var parent: Geometry? = null

    private data class LineIndices(var a: Int, var b: Int, var color: Color)

    /**
     * List of vertices, with resolved indices.
     * The list might get invalidated over time.
     * To query vertex points, this geometry must be registered firstly into a matrix buffer.
     */
    val vertices: List<Vertex>
        get() = let {
            lines.flatMap {
                listOf(
                        Vertex(positions[it.a], it.color, modelIndex),
                        Vertex(positions[it.b], it.color, modelIndex)
                )
            }
        }

    /**
     * Thrown when trying to transform the geometry while not registered into a matrix buffer.
     */
    inner class NotRegisteredIntoMatrixBufferException
        : Exception("Geometry $this not registered into matrix buffer")

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
         */
        inline fun geometrical(f: () -> Unit) {
            geometricalCounter++
            f()
            geometricalCounter--
            if (0 == geometricalCounter) {
                onGeometryChangedListeners.fire()
            }
        }

        /**
         * Every time this counter reaches 0, geometry change listeners are fired.
         * The counter can never be negative, and is incremented/decremented automatically
         * on calls to [geometrical].
         */
        @Suppress("MemberVisibilityCanBePrivate")
        @PublishedApi
        internal var geometricalCounter = 0
            set(value) {
                field = max(value, 0)
            }

    }

    /**
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    protected fun addPosition(position: Vector) {
        geometrical {
            positions += position
        }
    }

    /**
     * Add a lines from point [a] to point [b].
     */
    protected fun addLine(a: Int, b: Int, color: Color = baseColor) {
        geometrical {
            lines += LineIndices(a, b, color)
        }
    }

    /**
     * Colorize the [lineIndex]th line to [color].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    fun colorizeLine(lineIndex: Int, color: Color) {
        geometrical {
            lines[lineIndex].color = color
        }
    }

    /**
     * Colorize the [lineIndex]th line to [baseColor].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    fun decolorizeLine(lineIndex: Int) {
        geometrical {
            colorizeLine(lineIndex, baseColor)
        }
    }

    /**
     * Remove all geometry data.
     */
    protected fun clearGeometry() {
        geometrical {
            positions.clear()
            lines.clear()
        }
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate
     * counterparts.
     * @param keepColors The generated copy will have matching colors to the line set it originated from.
     * @param connectorColor Color of the lines connecting the original and generated lines.
     */
    fun extrude(
            direction: Vector,
            keepColors: Boolean = false,
            connectorColor: Color = baseColor
    ) {
        geometrical {
            val size = positions.size
            positions += positions.map { it + direction }
            lines += lines.map {
                LineIndices(
                        it.a + size,
                        it.b + size,
                        if (keepColors) it.color else baseColor
                )
            }
            for (i in 0 until size) {
                addLine(i, i + size, connectorColor)
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

    /**
     * Rotate in the zx plane around [theta].
     */
    fun rotationZX(theta: Float) {
        localMemory.rotation(ROTATION_ZX_MATRIX, 2, 0, theta)
    }

    /**
     * Rotate in the yx plane around [theta].
     */
    fun rotationYX(theta: Float) {
        localMemory.rotation(ROTATION_YX_MATRIX, 1, 0, theta)
    }

    /**
     * Translate by [v].
     */
    fun translate(v: Vector) {
        localMemory.translation(TRANSLATION_MATRIX, v)
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
