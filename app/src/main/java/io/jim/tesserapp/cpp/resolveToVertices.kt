package io.jim.tesserapp.cpp

import io.jim.tesserapp.geometry.ColorResolver
import io.jim.tesserapp.geometry.Line

fun Line.resolveToVertices(colorResolver: ColorResolver): List<Vertex> =
        points.map { Vertex(it, colorResolver(color)) }
