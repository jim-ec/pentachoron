package io.jim.tesserapp.cpp

import io.jim.tesserapp.geometry.ColorResolver
import io.jim.tesserapp.geometry.Line

fun resolveLineToVertices(line: Line, colorResolver: ColorResolver): List<Vertex> =
        line.points.map { Vertex(it, colorResolver(line.color)) }
