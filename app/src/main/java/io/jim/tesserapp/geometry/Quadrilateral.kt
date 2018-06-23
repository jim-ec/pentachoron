package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

fun quadrilateral(
        a: VectorN,
        b: VectorN,
        c: VectorN,
        d: VectorN,
        color: Geometry.Color = Geometry.Color.PRIMARY
) = listOf(
        Line(a, b, color),
        Line(b, c, color),
        Line(c, d, color),
        Line(d, a, color)
)
