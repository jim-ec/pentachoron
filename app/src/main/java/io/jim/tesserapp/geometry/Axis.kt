package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

/**
 * Axis indicator geometry.
 */
fun axis() = listOf(
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(1.0, 0.0, 0.0, 0.0), Geometry.Color.X),
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 1.0, 0.0, 0.0), Geometry.Color.Y),
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 0.0, 1.0, 0.0), Geometry.Color.Z),
        Line(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 0.0, 0.0, 1.0), Geometry.Color.Q)
)
