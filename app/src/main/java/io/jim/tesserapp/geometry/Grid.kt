package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector4dh

/**
 * Geometry representing a flat, orthogonal grid.
 * The center is cut out so that an [Axis] geometry fits into the grid without overlapping lines.
 */
class Grid(
        name: String = "Grid",
        color: Color
) : Geometry(name, color) {

    init {
        for (i in -5..-1) {
            addLine(Vector4dh(i.toDouble(), 0.0, -5.0, 0.0), Vector4dh(i.toDouble(), 0.0, 5.0, 0.0))
            addLine(Vector4dh(-5.0, 0.0, i.toDouble(), 0.0), Vector4dh(5.0, 0.0, i.toDouble(), 0.0))
        }
        for (i in 1..5) {
            addLine(Vector4dh(i.toDouble(), 0.0, -5.0, 0.0), Vector4dh(i.toDouble(), 0.0, 5.0, 0.0))
            addLine(Vector4dh(-5.0, 0.0, i.toDouble(), 0.0), Vector4dh(5.0, 0.0, i.toDouble(), 0.0))
        }

        addLine(Vector4dh(-5.0, 0.0, 0.0, 0.0), Vector4dh(0.0, 0.0, 0.0, 0.0))
        addLine(Vector4dh(1.0, 0.0, 0.0, 0.0), Vector4dh(5.0, 0.0, 0.0, 0.0))

        addLine(Vector4dh(0.0, 0.0, -5.0, 0.0), Vector4dh(0.0, 0.0, 0.0, 0.0))
        addLine(Vector4dh(0.0, 0.0, 1.0, 0.0), Vector4dh(0.0, 0.0, 5.0, 0.0))
    }

}
