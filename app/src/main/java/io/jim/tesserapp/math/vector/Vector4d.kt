package io.jim.tesserapp.math.vector

/**
 * A 4d vector.
 */
class Vector4d(x: Float, y: Float, z: Float, q: Float) : VectorN(x, y, z, q) {

    /**
     * Construct a vector with all components set to zero.
     */
    constructor() : this(0f, 0f, 0f, 0f)

    /**
     * X-component.
     */
    var x by IndexAlias(0)

    /**
     * Y-component.
     */
    var y by IndexAlias(1)

    /**
     * Z-component.
     */
    var z by IndexAlias(2)

    /**
     * Q-component.
     */
    var q by IndexAlias(3)

}
