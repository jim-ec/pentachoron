package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.graphics.Vertex
import io.jim.tesserapp.math.transform.Matrix
import io.jim.tesserapp.math.vector.Vector3d
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

    private val positions = ArrayList<Vector3d>()

    private val lines = ArrayList<LineIndices>()

    private val rotationMatrixY = Matrix(4)
    private val rotationMatrixX = Matrix(4)
    private val rotationMatrixZ = Matrix(4)
    private val rotationMatrixZY = Matrix(4)
    private val rotationMatrix = Matrix(4)
    private val translationMatrix = Matrix(4)

    val modelMatrix = Matrix(4)

    /**
     * Rotation around the x, y and z axis.
     */
    val rotation = Vector3d(0f, 0f, 0f)

    /**
     * Translation.
     */
    val translation = Vector3d(0f, 0f, 0f)

    /**
     * List of vertices, with resolved indices.
     * The list might get invalidated over time.
     * To query vertex points, this geometry must be registered firstly into a matrix buffer.
     */
    fun vertices(modelIndex: Int): List<Vertex> =
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
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    protected fun addPosition(position: Vector3d) {
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
            direction: Vector3d,
            keepColors: Boolean = false,
            connectorColor: Color = baseColor
    ) {
        val size = positions.size

        for (i in 0 until size) {
            positions += Vector3d().apply {
                copyFrom(positions[i])
                this += direction
            }
        }

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

        // Rotation:
        rotationMatrixX.rotation(a = 1, b = 2, radians = rotation.x)
        rotationMatrixY.rotation(a = 2, b = 0, radians = rotation.y)
        rotationMatrixZ.rotation(a = 0, b = 1, radians = rotation.z)

        rotationMatrixZY.multiplication(
                rhs = rotationMatrixZ,
                lhs = rotationMatrixY
        )

        rotationMatrix.multiplication(
                lhs = rotationMatrixZY,
                rhs = rotationMatrixX
        )

        // Translation:
        translationMatrix.translation(translation)

        // Model transform:
        modelMatrix.multiplication(
                lhs = rotationMatrix,
                rhs = translationMatrix
        )

    }

    override fun toString() = name

}
