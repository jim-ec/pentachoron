package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * A geometry containing a list of lines.
 */
class Lines(name: String, baseColor: Color = Color.BLACK) : Geometry(name, baseColor) {

    private var index = 0

    /**
     * Add a line from point [a] to point [b].
     */
    fun addLine(a: Vector, b: Vector, color: Color = baseColor) {
        geometrical {
            addPosition(a)
            addPosition(b)
            addLine(index++, index++, color)
        }
    }

    /**
     * Remove all lines.
     */
    fun clearLines() {
        geometrical {
            clearGeometry()
            index = 0
        }
    }

}
