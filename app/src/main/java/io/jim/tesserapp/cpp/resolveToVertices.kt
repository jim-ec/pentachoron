package io.jim.tesserapp.cpp

import io.jim.tesserapp.geometry.Line
import io.jim.tesserapp.geometry.SymbolicColorMapping

fun resolveLineToVertices(line: Line, colorMapping: SymbolicColorMapping): List<Vertex> =
        line.points.map { Vertex(it, colorMapping[line.color]) }
