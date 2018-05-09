package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector3d

/**
 * A geometry containing a list of lines.
 */
class Lines(name: String, baseColor: Color = Color.BLACK) : Geometry(name, baseColor) {

    private var index = 0

    /**
     * Add a line from point [a] to point [b].
     */
    fun addLine(a: Vector3d, b: Vector3d, color: Color = baseColor) {
        addPosition(a)
        addPosition(b)
        addLine(index++, index++, color)
    }

    /**
     * Remove all lines.
     */
    fun clearLines() {
        clearGeometry()
        index = 0
    }

}
