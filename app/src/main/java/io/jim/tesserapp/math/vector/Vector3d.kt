package io.jim.tesserapp.math.vector

/**
 * A 3d vector.
 */
open class Vector3d(x: Double, y: Double, z: Double) : VectorN(x, y, z) {
    
    /**
     * Construct a vector with all components set to zero.
     */
    constructor() : this(0.0, 0.0, 0.0)
    
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
     * Compute the vector product of [lhs] and [rhs], storing the result in this vector.
     */
    fun crossed(lhs: Vector3d, rhs: Vector3d) {
        x = lhs.y * rhs.z - lhs.z * rhs.y
        y = lhs.z * rhs.x - lhs.x * rhs.z
        z = lhs.x * rhs.y - lhs.y * rhs.x
    }
    
}
