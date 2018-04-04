package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * A geometry containing a list of lines.
 */
class Lines(color: Color) : Geometry(color) {

    private var index = 0

    /**
     * Add a line from point [a] to point [b].
     */
    fun addLine(a: Vector, b: Vector) {
        addPoints(a, b)
        addLine(index++, index++)
    }

}
