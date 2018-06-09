package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.math.vector.Vector3dh

/**
 * Responsible for computing a look-at matrix.
 */
class LookAtMatrix {

    private val matrix = Matrix(4)
    private val forward = Vector3dh()
    private val right = Vector3dh()
    private val up = Vector3dh()
    private val negatedEye = Vector3dh()
    private val base = Vector3dh()

    /**
     * Compute view matrix.
     * @param eye Eye position.
     * @param target Target position.
     * @param refUp Up direction of camera.
     * @return The view matrix. Each invocation returns the same matrix with updated values.
     */
    operator fun invoke(eye: Vector3d, target: Vector3d, refUp: Vector3d): Matrix {

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

        matrix[0, 0] = right.x
        matrix[0, 1] = right.y
        matrix[0, 2] = right.z

        matrix[1, 0] = up.x
        matrix[1, 1] = up.y
        matrix[1, 2] = up.z

        matrix[2, 0] = forward.x
        matrix[2, 1] = forward.y
        matrix[2, 2] = forward.z

        matrix[3, 0] = 0.0
        matrix[3, 1] = 0.0
        matrix[3, 2] = 0.0
        matrix[3, 3] = 1.0

        matrix.transpose()

        base.multiplication(
                lhs = negatedEye,
                rhs = matrix
        )
        for (col in 0 until 3)
            matrix[3, col] = base[col]

        return matrix
    }

}
