package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * Axis indicator geometry.
 */
class Axis(
        name: String = "Axis",
        xAxisColor: Color,
        yAxisColor: Color,
        zAxisColor: Color
) : Geometry(name) {

    init {
        addLine(Vector4dh(0f, 0f, 0f, 0f), Vector4dh(1f, 0f, 0f, 0f), xAxisColor)
        addLine(Vector4dh(0f, 0f, 0f, 0f), Vector4dh(0f, 1f, 0f, 0f), yAxisColor)
        addLine(Vector4dh(0f, 0f, 0f, 0f), Vector4dh(0f, 0f, 1f, 0f), zAxisColor)
    }

}
