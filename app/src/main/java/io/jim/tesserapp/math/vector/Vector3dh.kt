package io.jim.tesserapp.math.vector

/**
 * A homogeneous 3d vector. Since it is effectively a 4D vector, it can only be multiplied
 * with 4xN matrices.
 */
class Vector3dh(x: Float, y: Float, z: Float) : Vector3d(x, y, z) {

    /**
     * Construct a vector with all components set to zero, except [w].
     */
    constructor() : this(0f, 0f, 0f)

    /**
     * W-component. This is always 1.
     * Setting this value will lead to homogeneous division.
     */
    private var w = 1f
        set(value) {
            this /= w
        }

}
