package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * A quadrilateral geometry, with four sides.
 */
class Quadrilateral(
        name: String,
        a: Vector, b: Vector, c: Vector, d: Vector,
        baseColor: Color = Color.BLACK
) : Geometry(name, baseColor) {

    init {
        geometrical {
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

}
