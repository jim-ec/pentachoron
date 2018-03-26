package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Vector

class Quadrilateral(a: Vector, b: Vector, c: Vector, d: Vector, color: Color) : Geometry(color) {

    init {
        add(a)
        add(b)

        add(b)
        add(c)

        add(c)
        add(d)

        add(d)
        add(a)
    }

}