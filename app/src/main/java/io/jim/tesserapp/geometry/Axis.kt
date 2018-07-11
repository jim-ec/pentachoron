package io.jim.tesserapp.geometry

/**
 * Axis indicator geometry.
 */
fun axis() = listOf(
        Line(Position(0.0, 0.0, 0.0, 0.0), Position(1.0, 0.0, 0.0, 0.0)),
        Line(Position(0.0, 0.0, 0.0, 0.0), Position(0.0, 1.0, 0.0, 0.0)),
        Line(Position(0.0, 0.0, 0.0, 0.0), Position(0.0, 0.0, 1.0, 0.0)),
        Line(Position(0.0, 0.0, 0.0, 0.0), Position(0.0, 0.0, 0.0, 1.0))
)
