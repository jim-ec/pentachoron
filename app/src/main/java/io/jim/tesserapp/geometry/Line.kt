package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

/**
 * Represents a line by connecting two positions.
 * These positions are referred by the two indices [startIndex] and [endIndex].
 *
 * @param positions List of positions into which the indices point.
 *
 * @property startIndex Index to position at which the line starts.
 * @property endIndex Index to position at which the line ends.
 * @property color Line color.
 */
class Line<out T : VectorN>(
        positions: List<T>,
        val startIndex: Int,
        val endIndex: Int,
        var color: Geometry.Color
) {
    
    /**
     * Start point.
     */
    val start = positions[startIndex]
    
    /**
     * End point.
     */
    val end = positions[endIndex]
    
    /**
     * Calls [f] for both the start position and the end position.
     */
    inline fun forEachPosition(f: (position: T) -> Unit) {
        f(start)
        f(end)
    }
    
}
