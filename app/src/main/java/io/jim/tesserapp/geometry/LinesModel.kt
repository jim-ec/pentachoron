package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.Color
import io.jim.tesserapp.math.Vector

/**
 * Represent a model of lines.
 */
class LinesModel(
        color: Color,
        lines: Array<Pair<Vector, Vector>>
) : Model(
        color,
        (lines.flatMap { (first, second) -> listOf(first, second) }).toTypedArray(),
        Array(lines.size) { it -> Pair(it * 2, it * 2 + 1) }
)
