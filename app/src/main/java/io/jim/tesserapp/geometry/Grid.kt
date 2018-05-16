package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.vector.Vector3d

/**
 * Geometry representing a flat, orthogonal grid.
 * The center is cut out so that an [Axis] geometry fits into the grid without overlapping lines.
 */
class Grid(
        name: String = "Grid",
        color: Color
) : Lines(name, color) {

    init {
        for (i in -5..5) {
            addLine(Vector3d(i.toFloat(), 0f, -5f), Vector3d(i.toFloat(), 0f, 5f))
            addLine(Vector3d(-5f, 0f, i.toFloat()), Vector3d(5f, 0f, i.toFloat()))
        }
        for (i in 1..5) {
            addLine(Vector3d(i.toFloat(), 0f, -5f), Vector3d(i.toFloat(), 0f, 5f))
            addLine(Vector3d(-5f, 0f, i.toFloat()), Vector3d(5f, 0f, i.toFloat()))
        }

        addLine(Vector3d(-5f, 0f, 0f), Vector3d(0f, 0f, 0f))
        addLine(Vector3d(1f, 0f, 0f), Vector3d(5f, 0f, 0f))

        addLine(Vector3d(0f, 0f, -5f), Vector3d(0f, 0f, 0f))
        addLine(Vector3d(0f, 0f, 1f), Vector3d(0f, 0f, 5f))
    }

}
