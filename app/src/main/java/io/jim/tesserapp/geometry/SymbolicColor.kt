package io.jim.tesserapp.geometry

/**
 * Symbolic colors.
 *
 * Geometries are colored indirectly using this palette.
 * The actual color integer is not relevant to the geometry.
 *
 * This is used to implement dynamic coloring when switching themes, without having to
 * rebuild the geometry just to change the color.
 */
enum class SymbolicColor {
    PRIMARY,
    ACCENT,
    X,
    Y,
    Z,
    Q
}

typealias ColorResolver = (color: SymbolicColor) -> Int
