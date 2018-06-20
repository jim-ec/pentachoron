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
    override val cols = 5
    
    /**
     * The dimension string should underline that the vector is not actually 4d.
     */
    override val dimensionString = "4dh"
    
    /**
     * X-component.
     */
    var x: Double
        get() = this[0]
        set(value) {
            this[0] = value
        }
    
    /**
     * Y-component.
     */
    var y: Double
        get() = this[1]
        set(value) {
            this[1] = value
        }
    
    /**
     * Z-component.
     */
    var z: Double
        get() = this[2]
        set(value) {
            this[2] = value
        }
    
    /**
     * Q-component.
     */
    var q: Double
        get() = this[3]
        set(value) {
            this[3] = value
        }
    
    /**
     * W-component. This is always 1.
     * Setting this value will lead to w-division.
     */
    private inline var w: Double
        get() = 1.0
        set(value) {
            this /= value
        }
    
    /**
     * Intercept getting the w-component, which is always 1.
     */
    override fun get(row: Int, col: Int): Double {
        return if (col < 3)
            super.get(row, col)
        else
            w
    }
    
    /**
     * Intercept setting values to the fourth column, which will effectively lead to w-division.
     */
    override fun set(row: Int, col: Int, value: Double) {
        if (col < 3)
            super.set(row, col, value)
        else {
            w = value
        }
    }
    
}
