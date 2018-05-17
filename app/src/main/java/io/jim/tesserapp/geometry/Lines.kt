package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * A geometry containing a list of lines.
 */
open class Lines(name: String, baseColor: Color = Color.BLACK) : Geometry(name, baseColor) {

    private var index = 0

    /**
     * Add a line from point [a] to point [b].
     */
    fun addLine(a: Vector4dh, b: Vector4dh, color: Color = baseColor) {
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
