package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

data class Line(
        val start: VectorN,
        val end: VectorN,
        val color: SymbolicColor = SymbolicColor.PRIMARY
)

fun Line.resolveToVertices(colorResolver: ColorResolver): List<Pair<VectorN, Int>> =
        listOf(start to colorResolver(color), end to colorResolver(color))
