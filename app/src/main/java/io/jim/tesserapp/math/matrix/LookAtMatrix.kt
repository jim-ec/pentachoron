package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.Vector3dh

/**
 * Responsible for computing a look-at matrix.
 */
class LookAtMatrix {

    private val matrix = Matrix(4)
    private val forward = Vector3dh().apply { x = 1.0 }
    private val right = Vector3dh()
    private val up = Vector3dh()
    private val negatedEye = Vector3dh()
    private val base = Vector3dh()

    /**
     * Compute view matrix.
     * @param distance Position of eye along the x-axis.
     * @param refUp Up direction of camera.
     * @return The view matrix. Each invocation returns the same matrix with updated values.
     */
    fun computed(distance: Double, refUp: Vector3dh): Matrix {

        right.apply {
            crossed(refUp, forward)
            normalize()
        }

        up.apply {
            crossed(forward, right)
            normalize()
        }

        negatedEye.x = -distance

        matrix.identity()

        matrix[0, 0] = right.x
        matrix[0, 1] = right.y
        matrix[0, 2] = right.z

        matrix[1, 0] = up.x
        matrix[1, 1] = up.y
        matrix[1, 2] = up.z

        matrix[2, 0] = forward.x
        matrix[2, 1] = forward.y
        matrix[2, 2] = forward.z

        matrix.transpose()

        base.multiplication(
                lhs = negatedEye,
                rhs = matrix
        )
        matrix.translation(base)

        return matrix
    }

}
