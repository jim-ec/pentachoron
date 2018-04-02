package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Vector

class Line(a: Vector, b: Vector, color: Color) : Geometry(color) {

    init {
        addPoints(a, b)
        addLine(0, 1)
    }

}
