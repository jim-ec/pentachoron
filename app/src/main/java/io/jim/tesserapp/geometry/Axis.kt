package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

/**
 * Axis indicator geometry.
 */
fun Geometry.axis() {
    addLine(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(1.0, 0.0, 0.0, 0.0), Geometry.Color.X)
    addLine(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 1.0, 0.0, 0.0), Geometry.Color.Y)
    addLine(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 0.0, 1.0, 0.0), Geometry.Color.Z)
    addLine(VectorN(0.0, 0.0, 0.0, 0.0), VectorN(0.0, 0.0, 0.0, 1.0), Geometry.Color.Q)
}
