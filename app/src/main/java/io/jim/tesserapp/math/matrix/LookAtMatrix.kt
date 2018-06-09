package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.Vector3d
import io.jim.tesserapp.math.vector.Vector3dh

/**
 * Return a view-matrix generator.
 *
 * The generator expects these parameters: *eye*, *target*, *refUp*.
 *
 * The camera is constructed in such a way that it is positioned at *eye*, points to a *target*
 * and the upper edge is oriented in the *refUp* direction.
 */
fun lookAtMatrixGenerator(): (eye: Vector3d,
                              target: Vector3d,
                              refUp: Vector3d) -> Matrix {

    val matrix = Matrix(4)
    val forward = Vector3dh()
    val right = Vector3dh()
    val up = Vector3dh()
    val negatedEye = Vector3dh()
    val base = Vector3dh()

    return { eye, target, refUp ->

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

        matrix
    }
}
