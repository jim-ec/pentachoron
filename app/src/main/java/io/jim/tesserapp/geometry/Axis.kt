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
        addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(1.0, 0.0, 0.0, 0.0), xAxisColor)
        addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 1.0, 0.0, 0.0), yAxisColor)
        addLine(Vector4dh(0.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 0.0, 1.0, 0.0), zAxisColor)
    }

}
