package io.jim.tesserapp.math.vector

/**
 * A homogeneous 3d vector. Since it is effectively a 4D vector, it can only be multiplied
 * with 4xN matrices.
 */
class Vector3dh(x: Double, y: Double, z: Double) : VectorN(x, y, z) {
    
    /**
     * Construct a vector with all components set to zero.
     */
    constructor() : this(0.0, 0.0, 0.0)
    
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
        }
    }

    /**
     * Compute the vector product of [lhs] and [rhs], storing the result in this vector.
     */
    override fun crossed(lhs: VectorN, rhs: VectorN) {
        x = lhs.y * rhs.z - lhs.z * rhs.y
        y = lhs.z * rhs.x - lhs.x * rhs.z
        z = lhs.x * rhs.y - lhs.y * rhs.x
    }
    
}
