package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Vector

class Line(a: Vector, b: Vector, color: Color) : Geometry(color) {

    init {
        add(a)
        add(b)
    }

}
