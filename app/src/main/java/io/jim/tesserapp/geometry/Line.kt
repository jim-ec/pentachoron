package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Point

class Line(a: Point, b: Point, color: Color) : Geometry(color) {

    init {
        addPoints(a, b)
        addLine(0, 1)
    }

}
