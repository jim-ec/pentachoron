package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector3d

/**
 * Axis indicator geometry.
 */
class Axis(
        name: String = "Axis",
        xAxisColor: Color,
        yAxisColor: Color,
        zAxisColor: Color
) : Lines(name) {

    init {
        addLine(Vector3d(0f, 0f, 0f), Vector3d(1f, 0f, 0f), xAxisColor)
        addLine(Vector3d(0f, 0f, 0f), Vector3d(0f, 1f, 0f), yAxisColor)
        addLine(Vector3d(0f, 0f, 0f), Vector3d(0f, 0f, 1f), zAxisColor)
    }

}
