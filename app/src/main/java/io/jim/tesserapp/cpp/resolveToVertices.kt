package io.jim.tesserapp.cpp

import io.jim.tesserapp.cpp.graphics.Color
import io.jim.tesserapp.geometry.Line

fun resolveLineToVertices(line: Line, color: Color): List<Vertex> =
        line.points.map { Vertex(it, color) }
