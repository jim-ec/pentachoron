package io.jim.tesserapp.entity

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

internal typealias Line = Pair<Int, Int>

/**
 * A spatial object, containing geometry info and having a color.
 */
open class Model(

        /**
         * Color of this geometry.
         */
        val color: Color,

        /**
         * List of points.
         */
        val points: Array<Vector>,

        /**
         * List of lines, represented by indices to two points.
         */
        val lines: Array<Line>

) {

    companion object {

        /**
         * Listener [f] is fired every time a single point or line is added.
         */
        fun addGeometryChangedListener(f: () -> Unit) {
            onGeometryChangedListeners.add(f)
        }

        private val onGeometryChangedListeners = ArrayList<() -> Unit>()

    }

    /**
     * Thrown whenever a point with an invalid index is accessed.
     */
    inner class NoSuchIndexException(index: Int) :
            IndexOutOfBoundsException("No point with such an index: $index (${points.size} points)")

    /**
     * Set the point at [index] to [value].
     * @throws NoSuchIndexException If index is invalid.
     */
    operator fun set(index: Int, value: Vector) {
        try {
            points[index] = value
        } catch (_: IndexOutOfBoundsException) {
            throw NoSuchIndexException(index)
        }
        onGeometryChangedListeners.fire()
    }

    /**
     * Get the point at [index].
     * @throws NoSuchIndexException If index is invalid.
     */
    operator fun get(index: Int): Vector {
        try {
            return points[index]
        } catch (_: IndexOutOfBoundsException) {
            throw NoSuchIndexException(index)
        }
    }

}
