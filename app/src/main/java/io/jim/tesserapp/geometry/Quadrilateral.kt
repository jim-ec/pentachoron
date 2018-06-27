package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.vector.VectorN

fun quadrilateral(
        a: VectorN,
        b: VectorN,
        c: VectorN,
        d: VectorN
) = listOf(
        Line(a, b),
        Line(b, c),
        Line(c, d),
        Line(d, a)
)
