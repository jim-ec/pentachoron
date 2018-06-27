package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.vector.VectorN

/**
 * Axis indicator geometry.
 */
fun axis() = listOf(
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(1.0, 0.0, 0.0, 0.0)),
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 1.0, 0.0, 0.0)),
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 0.0, 1.0, 0.0)),
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 0.0, 0.0, 1.0))
)
