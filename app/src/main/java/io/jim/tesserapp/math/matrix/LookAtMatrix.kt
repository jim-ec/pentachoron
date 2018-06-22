package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.VectorN

/**
 * Responsible for computing a look-at matrix.
 */
class LookAtMatrix {

    private val matrix = Matrix(4)
    private val forward = VectorN(1.0, 0.0, 0.0)

    /**
     * Compute view matrix.
     * @param distance Position of eye along the x-axis.
     * @param refUp Up direction of camera.
     * @return The view matrix. Each invocation returns the same matrix with updated values.
     */
    fun computed(distance: Double, refUp: VectorN): Matrix {

        val right = (refUp cross forward).normalized()
        val up = (forward cross right).normalized()

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
        matrix.translation(VectorN(-distance, 0.0, 0.0) * matrix)

        return matrix
    }

}
