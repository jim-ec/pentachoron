package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * A quadrilateral geometry, with four sides.
 */
class Quadrilateral(
        name: String,
        a: Vector4dh, b: Vector4dh, c: Vector4dh, d: Vector4dh,
        baseColor: Color = Color.BLACK
) : Geometry(name, baseColor) {

    init {
        addPosition(a)
        addPosition(b)
        addPosition(c)
        addPosition(d)
        addLine(0, 1)
        addLine(1, 2)
        addLine(2, 3)
        addLine(3, 0)
    }

}
