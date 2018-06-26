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

data class SymbolicColorMapping(
        val primary: Int,
        val accent: Int,
        val x: Int,
        val y: Int,
        val z: Int,
        val q: Int
) {
    
    operator fun get(color: SymbolicColor) = when (color) {
        SymbolicColor.PRIMARY -> primary
        SymbolicColor.ACCENT -> accent
        SymbolicColor.X -> x
        SymbolicColor.Y -> y
        SymbolicColor.Z -> z
        SymbolicColor.Q -> q
    }
    
}
