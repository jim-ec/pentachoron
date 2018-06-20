package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.Vector4dh

/**
 * Axis indicator geometry.
 */
fun Geometry.axis() {
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(1.0, 0.0, 0.0, 0.0), Geometry.Color.X)
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 1.0, 0.0, 0.0), Geometry.Color.Y)
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 0.0, 1.0, 0.0), Geometry.Color.Z)
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 0.0, 0.0, 1.0), Geometry.Color.Q)
}
