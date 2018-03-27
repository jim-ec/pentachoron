package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Point

class Triangle(a: Point, b: Point, c: Point, color: Color) : Geometry(color) {

    init {
        addPoints(a, b, c)
        addLine(0, 1)
        addLine(1, 2)
        addLine(2, 0)
    }

}