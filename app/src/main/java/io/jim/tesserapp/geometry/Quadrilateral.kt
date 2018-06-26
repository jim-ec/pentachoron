package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.vector.VectorN

fun quadrilateral(
        a: VectorN,
        b: VectorN,
        c: VectorN,
        d: VectorN,
        color: SymbolicColor = SymbolicColor.PRIMARY
) = listOf(
        Line(a, b, color),
        Line(b, c, color),
        Line(c, d, color),
        Line(d, a, color)
)
