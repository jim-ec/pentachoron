package io.jim.tesserapp.geometry

import android.graphics.Color.BLACK
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * A geometrical structure consisting of vertices.
 */
class Geometry {

    companion object {

        /**
         * The volume onto which 4 dimensional vectors are projected.
         * This value should be well chosen, as no vector should ever have such a q-value,
         * since that will lead to projection into infinity.
         */
        const val Q_PROJECTION_VOLUME = 1.0

    }

    /**
     * List containing all positions.
     */
    private val positions = ArrayList<Vector4dh>()

    /**
     * List containing all lines constructed from [positions] using indices.
     */
    val lines = ArrayList<Line<Vector4dh>>()

    /**
     * This geometry's name.
     */
    var name = ""

    /**
     * Color of this geometry.
     */
    var baseColor = BLACK

    /**
     * Model-transform.
     */
    val transform = Transform()

    /**
     * Add a series of vertices.
     * The actual lines are drawn from indices to these vertices.
     */
    private fun addPosition(position: Vector4dh) {
        positions += position
    }

    /**
     * Add a line spanning between two positions.
     * @param a Index to the start position.
     * @param b Index to the end position.
     * @param color Line color, defaults to geometry's [baseColor].
     */
    private fun addLine(a: Int, b: Int, color: Int = baseColor) {
        lines += Line(positions, a, b, color)
    }

    /**
     * Add a line from position [a] to position [b].
     * This actually creates to new positions.
     * @param a Starting position.
     * @param b End position.
     * @param color Line color, defaults to geometry's [baseColor].
     */
    fun addLine(a: Vector4dh, b: Vector4dh, color: Int = baseColor) {
        addPosition(a)
        addPosition(b)
        addLine(positions.lastIndex - 1, positions.lastIndex, color)
    }

    /**
     * Add a quadrilateral with four corner and an optional color.
     * @param a First corner.
     * @param b Second corner.
     * @param c Third corner.
     * @param d Fourth corner.
     * @param color Color of added lines. Defaults to [baseColor].
     */
    fun addQuadrilateral(
            a: Vector4dh,
            b: Vector4dh,
            c: Vector4dh,
            d: Vector4dh,
            color: Int = baseColor
    ) {
        addPosition(a)
        addPosition(b)
        addPosition(c)
        addPosition(d)
        addLine(0, 1, color)
        addLine(1, 2, color)
        addLine(2, 3, color)
        addLine(3, 0, color)
    }

    /**
     * Colorize the [lineIndex]th line to [color].
     * @throws IndexOutOfBoundsException If index is out of bounds.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun colorizeLine(lineIndex: Int, color: Int) {
        lines[lineIndex].color = color
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
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate
     * counterparts.
     * @param keepColors The generated copy will have matching colors to the line set it originated from.
     * @param connectorColor Color of the lines connecting the original and generated lines.
     */
    fun extrude(
            direction: Vector4dh,
            keepColors: Boolean = false,
            connectorColor: Int = baseColor
    ) {
        val size = positions.size

        for (i in 0 until size) {
            positions += Vector4dh().apply {
                copyFrom(positions[i])
                this += direction
            }
        }

        lines += lines.map {
            Line(
                    positions,
                    it.startIndex + size,
                    it.endIndex + size,
                    if (keepColors) it.color else baseColor
            )
        }
        for (i in 0 until size) {
            addLine(i, i + size, connectorColor)
        }
    }

    /**
     * Invoke [f] for each position and the color it's associated with.
     */
    inline fun forEachVertex(f: (position: Vector4dh, color: Int) -> Unit) {
        lines.forEach {
            f(it.start, it.color)
            f(it.end, it.color)
        }
    }

    /**
     * Represents this geometry in a string.
     */
    override fun toString() = name

}
