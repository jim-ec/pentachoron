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
     * Though this vector is 4d, it technically has 5 columns, including the virtual w-component.
     */
    override val cols = dimension + 1
    
    /**
     * Q-component.
     */
    var q: Double
        get() = this[3]
        set(value) {
            this[3] = value
        }
    
    /**
     * Intercept getting the w-component, which is always 1.
     */
    override fun get(row: Int, col: Int): Double {
        return if (col < dimension)
            super.get(row, col)
        else
            1.0
    }
    
    /**
     * Intercept setting values to the fourth column, which will effectively lead to w-division.
     */
    override fun set(row: Int, col: Int, value: Double) {
        if (col < dimension)
            super.set(row, col, value)
        else {
            x /= value
            y /= value
            z /= value
            q /= value
        }
    }
    
}
