package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * A geometry containing a list of lines.
 */
class Lines(name: String, baseColor: Color) : Geometry(name, baseColor) {

    private var index = 0

    /**
     * Add a line from point [a] to point [b].
     */
    fun addLine(a: Vector, b: Vector, color: Color = baseColor) {
        geometrical {
            addPoint(a, color)
            addPoint(b, color)
            addLine(index++, index++)
        }
    }

    /**
     * Remove all lines.
     */
    fun clearLines() {
        geometrical {
            clearPoints()
            index = 0
        }
    }

}
