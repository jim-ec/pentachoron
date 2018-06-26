package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

data class Line(
        val start: VectorN,
        val end: VectorN,
        val color: SymbolicColor = SymbolicColor.PRIMARY
) {
    
    val points = listOf(start, end)
    
}

fun Line.resolveToVertices(colorResolver: ColorResolver): List<Vertex> =
        points.map { Vertex(it, colorResolver(color)) }
