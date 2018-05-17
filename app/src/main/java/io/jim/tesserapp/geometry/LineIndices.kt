package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color

/**
 * Represents a line by connecting two positions.
 * These positions are referred by the two indices [from] and [to].
 * @property from Index to position at which the line starts.
 * @property to Index to position at which the line ends.
 * @property color Line color.
 */
data class LineIndices(
        val from: Int,
        val to: Int,
        var color: Color
) {

    /**
     * Calls [f] for both the start-position index as well as the end-position index.
     */
    inline fun forEachPosition(f: (positionIndex: Int) -> Unit) {
        f(from)
        f(to)
    }

}
