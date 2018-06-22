package io.jim.tesserapp.math.vector

/**
 * A 4d vector with an additional 5th homogeneous component called `w`.
 * The first 4 components are called: `x`, `y`, `z` and `q`.
 */
class Vector4dh(x: Double, y: Double, z: Double, q: Double) : VectorN(x, y, z, q) {
    
    /**
     * Construct a vector with all components set to zero.
     */
    constructor() : this(0.0, 0.0, 0.0, 0.0)
    
    /**
     * Q-component.
     */
    var q: Double
        get() = this[3]
        set(value) {
            this[3] = value
        }

}
