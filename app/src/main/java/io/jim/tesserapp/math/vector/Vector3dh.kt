package io.jim.tesserapp.math.vector

/**
 * A homogeneous 3d vector. Since it is effectively a 4D vector, it can only be multiplied
 * with 4xN matrices.
 */
class Vector3dh(x: Double, y: Double, z: Double) : Vector3d(x, y, z) {
    
    /**
     * Construct a vector with all components set to zero, except [w].
     */
    constructor() : this(0.0, 0.0, 0.0)
    
    /**
     * Though this vector is 3d, it technically has 4 columns, including the virtual w-component.
     */
    override val cols = 4
    
    /**
     * The dimension string should underline that the vector is not actually 3d.
     */
    override val dimensionString = "3dh"
    
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
    
    /**
     * W-component. This is always 1.
     * Setting this value will lead to w-division.
     */
    private inline var w: Double
        get() = 1.0
        set(value) {
            this /= value
        }
    
}
