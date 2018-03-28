package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Point

class Quadrilateral(a: Point, b: Point, c: Point, d: Point, color: Color) : Geometry(color) {

    init {
        addPoints(a, b, c, d)
        addLine(0, 1)
        addLine(1, 2)
        addLine(2, 3)
        addLine(3, 0)
    }

}