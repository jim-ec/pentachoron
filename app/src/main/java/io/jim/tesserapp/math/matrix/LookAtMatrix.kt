package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.math.vector.Vector3dh

/**
 * Represents a look-at transform. Most likely use-case is camera-transform.
 */
class LookAtMatrix : Matrix(4) {

    private val forward = Vector3dh()
    private val right = Vector3dh()
    private val up = Vector3dh()
    private val negatedEye = Vector3dh()
    private val base = Vector3dh()

    /**
     * Load a look at matrix.
     * The camera is constructed in such a way that it is positioned at [eye], Vectors to [target],
     * and the upper edge is oriented in the [refUp] Vector.
     * @param eye Eye position. W component should be 1.
     * @param target Target position. W component should be 1.
     * @param refUp Target position. W component should be 0.
     */
    fun lookAt(
            eye: Vector3d,
            target: Vector3d,
            refUp: Vector3d
    ) {

        forward.apply {
            copyFrom(eye)
            this -= target
            normalize()
        }

        right.apply {
            crossed(refUp, forward)
            normalize()
        }

        up.apply {
            crossed(forward, right)
            normalize()
        }

        negatedEye.apply {
            copyFrom(eye)
            negate()
        }

        this[0, 0] = right.x
        this[0, 1] = right.y
        this[0, 2] = right.z

        this[1, 0] = up.x
        this[1, 1] = up.y
        this[1, 2] = up.z

        this[2, 0] = forward.x
        this[2, 1] = forward.y
        this[2, 2] = forward.z

        this[3, 0] = 0.0
        this[3, 1] = 0.0
        this[3, 2] = 0.0
        this[3, 3] = 1.0

        transpose()

        base.multiplication(
                lhs = negatedEye,
                rhs = this
        )
        for (col in 0 until 3)
            this[3, col] = base[col]
    }

}
