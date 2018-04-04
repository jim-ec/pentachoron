package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * A quadrilateral geometry, with four sides.
 */
class Quadrilateral(a: Vector, b: Vector, c: Vector, d: Vector, color: Color)
    : Geometry(color) {

    init {
        addPoints(a, b, c, d)
        addLine(0, 1)
        addLine(1, 2)
        addLine(2, 3)
        addLine(3, 0)
    }

}
