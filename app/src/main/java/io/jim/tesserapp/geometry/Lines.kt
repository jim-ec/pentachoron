package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

class Lines(dimension: Int, color: Color) : Geometry(dimension, color) {

    private var index = 0

    fun addLine(a: Vector, b: Vector) {
        addPoints(a, b)
        addLine(index++, index++)
    }

}
