package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.VectorN

/**
 * Represents a line by connecting two positions.
 * These positions are referred by the two indices [from] and [to].
 * @property positions List of positions into which the indices point.
 * @property from Index to position at which the line starts.
 * @property to Index to position at which the line ends.
 * @property color Line color.
 */
data class Line<out T : VectorN>(
        val positions: List<T>,
        val from: Int,
        val to: Int,
        var color: Color
) {

    /**
     * Calls [f] for both the start position and the end position.
     */
    inline fun forEachPosition(f: (position: T) -> Unit) {
        f(positions[from])
        f(positions[to])
    }

}
