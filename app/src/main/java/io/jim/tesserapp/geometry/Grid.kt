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
        for (i in -5..5) {
            addLine(Vector4dh(i.toFloat(), 0f, -5f, 0f), Vector4dh(i.toFloat(), 0f, 5f, 0f))
            addLine(Vector4dh(-5f, 0f, i.toFloat(), 0f), Vector4dh(5f, 0f, i.toFloat(), 0f))
        }
        for (i in 1..5) {
            addLine(Vector4dh(i.toFloat(), 0f, -5f, 0f), Vector4dh(i.toFloat(), 0f, 5f, 0f))
            addLine(Vector4dh(-5f, 0f, i.toFloat(), 0f), Vector4dh(5f, 0f, i.toFloat(), 0f))
        }

        addLine(Vector4dh(-5f, 0f, 0f, 0f), Vector4dh(0f, 0f, 0f, 0f))
        addLine(Vector4dh(1f, 0f, 0f, 0f), Vector4dh(5f, 0f, 0f, 0f))

        addLine(Vector4dh(0f, 0f, -5f, 0f), Vector4dh(0f, 0f, 0f, 0f))
        addLine(Vector4dh(0f, 0f, 1f, 0f), Vector4dh(0f, 0f, 5f, 0f))
    }

}
