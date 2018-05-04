package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.util.ListenerList

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

    /**
     * Rotation around the x, y and z axis.
     */
    val rotation = Vector(0f, 0f, 0f, 0f)

    /**
     * Translation.
     */
    val translation = Vector(0f, 0f, 0f, 1f)

    /**
     * List of vertices, with resolved indices.
     * The list might get invalidated over time.
     * To query vertex points, this geometry must be registered firstly into a matrix buffer.
     */
    val vertices: List<Vertex>
        get() =
            lines.flatMap {
                listOf(
                        Vertex(positions[it.from], it.color, modelIndex),
                        Vertex(positions[it.to], it.color, modelIndex)
                )
            }

    /**
     * Listeners are fired every time a single point or line is added.
     */
    val onGeometryChangedListeners = ListenerList()

    private data class LineIndices(val from: Int, val to: Int, var color: Color)

    /**
     * Thrown when trying to transform the geometry while not registered into a matrix buffer.
     */
    inner class NotRegisteredIntoMatrixBufferException
        : RuntimeException("Geometry $this not registered into matrix buffer")

    private enum class MatrixIndex {
        LOCAL,
        ROTATION,
        ROTATION_X,
        ROTATION_WZY,
        ROTATION_Y,
        ROTATION_WZ,
        ROTATION_Z,
        ROTATION_W,
        TRANSLATION;

        val index: Int by lazy { MatrixIndex.values().indexOf(this) }
    }

    companion object {
        private val LOCAL_MATRICES_PER_GEOMETRY: Int by lazy { MatrixIndex.values().size }
    }

    /**
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    protected fun addPosition(position: Vector) {
        positions += position
        onGeometryChangedListeners.fire()
    }

    /**
     * Add a lines from point [a] to point [b].
     */
    protected fun addLine(a: Int, b: Int, color: Color = baseColor) {
        lines += LineIndices(a, b, color)
        onGeometryChangedListeners.fire()
    }

    /**
     * Colorize the [lineIndex]th line to [color].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("unused", "MemberVisibilityCanBePrivate")
    fun colorizeLine(lineIndex: Int, color: Color) {
        lines[lineIndex].color = color
        onGeometryChangedListeners.fire()
    }

    /**
     * Colorize the [lineIndex]th line to [baseColor].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("unused")
    fun decolorizeLine(lineIndex: Int) {
        colorizeLine(lineIndex, baseColor)
    }

    /**
     * Remove all geometry data.
     */
    protected fun clearGeometry() {
        positions.clear()
        lines.clear()
        onGeometryChangedListeners.fire()
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
        val size = positions.size
        positions += positions.map { it + direction }
        lines += lines.map {
            LineIndices(
                    it.from + size,
                    it.to + size,
                    if (keepColors) it.color else baseColor
            )
        }
        for (i in 0 until size) {
            addLine(i, i + size, connectorColor)
        }
    }

    /**
     * Recomputes all model matrices recursively.
     *
     * Since this recursive function computes model matrices for its children after it's done
     * with its one global matrix, it can safely access its parent's global model matrix,
     * since that is guaranteed to already be computed.
     */
    fun computeModelMatrix() {

        val globalMemory = globalMemory ?: throw NotRegisteredIntoMatrixBufferException()

        // Rotation:
        localMemory.rotation(MatrixIndex.ROTATION_X.index, 1, 2, rotation.x)
        localMemory.rotation(MatrixIndex.ROTATION_Y.index, 2, 0, rotation.y)
        localMemory.rotation(MatrixIndex.ROTATION_Z.index, 0, 1, rotation.z)
        localMemory.rotation(MatrixIndex.ROTATION_W.index, 3, 0, rotation.w)

        localMemory.multiply(
                lhs = MatrixIndex.ROTATION_Z.index, rhs = MatrixIndex.ROTATION_W.index,
                matrix = MatrixIndex.ROTATION_WZ.index)

        localMemory.multiply(
                lhs = MatrixIndex.ROTATION_Y.index, rhs = MatrixIndex.ROTATION_WZ.index,
                matrix = MatrixIndex.ROTATION_WZY.index)

        localMemory.multiply(
                lhs = MatrixIndex.ROTATION_X.index, rhs = MatrixIndex.ROTATION_WZY.index,
                matrix = MatrixIndex.ROTATION.index)

        // Translation:
        localMemory.translation(MatrixIndex.TRANSLATION.index, translation)

        // Local:
        localMemory.multiply(lhs = MatrixIndex.ROTATION.index, rhs = MatrixIndex.TRANSLATION.index,
                matrix = MatrixIndex.LOCAL.index)

        // Global:
        globalMemory.copy(source = MatrixIndex.LOCAL.index, sourceMemorySpace = localMemory)

    }

    override fun toString() = name

}
