package io.jim.tesserapp.geometry

fun quadrilateral(
        a: Position,
        b: Position,
        c: Position,
        d: Position
) = listOf(
        Line(a, b),
        Line(b, c),
        Line(c, d),
        Line(d, a)
)
