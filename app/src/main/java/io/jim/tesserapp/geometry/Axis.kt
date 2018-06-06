package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.ColorInt
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * Axis indicator geometry.
 * @param xAxisColor Color of x-axis.
 * @param yAxisColor Color of y-axis.
 * @param zAxisColor Color of z-axis.
 */
fun Geometry.axis(
        xAxisColor: ColorInt,
        yAxisColor: ColorInt,
        zAxisColor: ColorInt
) {
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(1.0, 0.0, 0.0, 0.0), xAxisColor)
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 1.0, 0.0, 0.0), yAxisColor)
    addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 0.0, 1.0, 0.0), zAxisColor)
}
