package io.jim.tesserapp.geometry

data class Position(
        val x: Double,
        val y: Double,
        val z: Double,
        val q: Double
) {
    /**
     * Copy constructor, cloning [rhs].
     */
    constructor(rhs: Position) : this(rhs.x, rhs.y, rhs.z, rhs.q)
    
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
