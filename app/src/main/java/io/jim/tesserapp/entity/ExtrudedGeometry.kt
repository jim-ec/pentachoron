package io.jim.tesserapp.entity

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.MatrixBuffer
import io.jim.tesserapp.math.Vector

/**
 * Represent a model, which is statically extruded in a given direction.
 *
 * Both vertex sets, the original as well as the replicate are given as index ranges.
 */
class ExtrudedGeometry(
        color: Color,
        points: Array<Vector>,
        lines: Array<Line>,
        direction: Vector,
        name: String,
        matrixBuffer: MatrixBuffer,
        matrixGlobal: Int,
        matrixOffset: Int
) : Geometry(
        color,

        // The original points and the ones to which 'direction' is added:
        points + points.map { it + direction },

        // The original lines, ...
        lines
                // ... the exact same line set but for the replicate point set ...
                + lines.map { Pair(it.first + points.size, it.second + points.size) }

                // ... and the connector lines:
                + Array(points.size) { Pair(it, it + points.size) },

        name, matrixBuffer, matrixGlobal, matrixOffset
) {

    /**
     * Index range describing the original, unmodified set of point.
     */
    val original = 0 until points.size

    /**
     * Index range describing the modified point set of points.
     */
    val replicate = points.size until 2 * points.size

}
