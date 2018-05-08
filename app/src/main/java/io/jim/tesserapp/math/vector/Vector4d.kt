package io.jim.tesserapp.math.vector

/**
 * A 4d vector.
 */
class Vector4d : VectorN(4) {

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
