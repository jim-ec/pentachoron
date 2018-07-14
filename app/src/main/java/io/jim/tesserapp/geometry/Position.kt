package io.jim.tesserapp.geometry

data class Position(
        val x: Double,
        val y: Double,
        val z: Double,
        val q: Double
) {
    
    /**
     * Return this vector component-wise added to [rhs].
     */
    operator fun plus(rhs: Position) = Position(
            x + rhs.x,
            y + rhs.y,
            z + rhs.z,
            q + rhs.q
    )
    
}
