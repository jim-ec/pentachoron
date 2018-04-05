package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * A spatial object, containing geometry info and having a color.
 */
open class Geometry(

        name: String,

        /**
         * Color of this geometry.
         */
        val color: Color

) : Spatial(name) {

    /**
     * List of points.
     */
    val points = ArrayList<Vector>()

    /**
     * List of lines, represented by indices to two points.
     */
    val lines = ArrayList<Pair<Int, Int>>()

    companion object {

        /**
         * Register a new geometry listener.
         *
         * Note that the listener is invoked every time a single point or line is added.
         */
        fun addGeometryChangedListener(f: () -> Unit) {
            onGeometryChangedListener.add(f)
        }

        private val onGeometryChangedListener = ArrayList<() -> Unit>()

    }

    /**
     * Add a series of points.
     * The actual lines are drawn from indices to these points.
     */
    fun addPoints(vararg p: Vector) {
        points += p
        onGeometryChangedListener.forEach { it() }
    }

    /**
     * Add a lines from point [a] to point [b].
     */
    fun addLine(a: Int, b: Int) {
        lines += Pair(a, b)
        onGeometryChangedListener.forEach { it() }
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate
     * counterparts.
     */
    fun extrude(direction: Vector) {
        val size = points.size
        points += points.map { it + direction }
        lines += lines.map { Pair(it.first + size, it.second + size) }
        for (i in 0 until size) {
            addLine(i, i + size)
        }
    }

}
