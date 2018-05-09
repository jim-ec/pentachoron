package io.jim.tesserapp.math.transform

import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.math.vector.Vector3dh
import io.jim.tesserapp.math.vector.VectorCache

class LookAtMatrix : Matrix(4) {

    private val cache = VectorCache { Vector3dh() }

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
        cache.startAcquiring()

        val forward = cache.acquire().apply {
            copyFrom(eye)
            this -= target
            normalize()
        }

        val right = cache.acquire().apply {
            crossed(refUp, forward)
            normalize()
        }

        val up = cache.acquire().apply {
            crossed(forward, right)
            normalize()
        }

        val negatedEye = cache.acquire().apply {
            copyFrom(eye)
            negate()
        }

        val base = cache.acquire()

        cache.endAcquiring()

        this[0, 0] = right.x
        this[0, 1] = right.y
        this[0, 2] = right.z

        this[1, 0] = up.x
        this[1, 1] = up.y
        this[1, 2] = up.z

        this[2, 0] = forward.x
        this[2, 1] = forward.y
        this[2, 2] = forward.z

        this[3, 0] = 0f
        this[3, 1] = 0f
        this[3, 2] = 0f
        this[3, 3] = 1f

        transpose()

        base.multiplication(
                lhs = negatedEye,
                rhs = this
        )
        for (col in 0 until 3)
            this[3, col] = base[col]
    }

}
