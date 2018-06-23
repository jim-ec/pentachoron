package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

data class Line(
        val start: VectorN,
        val end: VectorN,
        var color: SymbolicColor = SymbolicColor.PRIMARY
)
