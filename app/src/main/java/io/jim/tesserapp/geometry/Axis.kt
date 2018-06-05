package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * Axis indicator geometry.
 * @param name Name representing this geometry.
 * @param xAxisColor Color of x-axis.
 * @param yAxisColor Color of y-axis.
 * @param zAxisColor Color of z-axis.
 */
fun axis(
        name: String,
        xAxisColor: Color,
        yAxisColor: Color,
        zAxisColor: Color
) = Geometry(name).apply {
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(1.0, 0.0, 0.0, 0.0), xAxisColor)
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 1.0, 0.0, 0.0), yAxisColor)
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 0.0, 1.0, 0.0), zAxisColor)
}
