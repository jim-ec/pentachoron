package io.jim.tesserapp.geometry

import io.jim.tesserapp.entity.ListenerList
import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector
import kotlin.properties.Delegates

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

    /**
     * If not visible, this geometry is not drawn.
     * A change in visibility is considered as a geometry change, and therefore fires all
     * listeners from [onGeometryChangedListeners]
     */
    var visible: Boolean by Delegates.observable(true) { _, _, _ ->
        onGeometryChangedListeners.fire()
    }

    companion object {

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

}
