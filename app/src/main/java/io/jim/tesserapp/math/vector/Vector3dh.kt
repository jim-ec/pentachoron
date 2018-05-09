package io.jim.tesserapp.math.vector

/**
 * A homogeneous 3d vector. Since it is effectively a 4D vector, it can only be multiplied
 * with 4xN matrices.
 */
class Vector3dh : Vector3d() {

    /**
     * W-component. This is always 1.
     * Setting this value will lead to homogeneous division.
     */
    private var w = 1f
        set(value) {
            this /= w
        }

}
