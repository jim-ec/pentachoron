package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.Vector3d

/**
 * Returns a function which can compute a view matrix.
 *
 * The calculated view matrix is returned from that closure.
 */
fun viewMatrixGenerator(): (Double, Double, Double, Double) -> Matrix {

    val viewMatrix = Matrix(4)
    val matrixLookAtRotation = Matrix(4)
    val matrixRotation = Matrix(4)
    val matrixHorizontalRotation = Matrix(4)
    val matrixVerticalRotation = Matrix(4)
    val matrixLookAt = LookAtMatrix()
    val matrixScale = Matrix(4)

    val eye = Vector3d(1.0, 0.0, 0.0)
    val scale = Vector3d(1.0, 1.0, 1.0)

    val upVector = Vector3d(0.0, 1.0, 0.0)
    val target = Vector3d(0.0, 0.0, 0.0)

    return { distance, aspectRatio, horizontalRotation, verticalRotation ->

        matrixHorizontalRotation.rotation(2, 0, horizontalRotation)
        matrixVerticalRotation.rotation(0, 1, verticalRotation)
        matrixRotation.multiplication(matrixHorizontalRotation, matrixVerticalRotation)

        eye.x = distance

        matrixLookAt.lookAt(
                eye = eye,
                target = target,
                refUp = upVector
        )

        scale.y = aspectRatio
        matrixScale.scale(scale)

        matrixLookAtRotation.multiplication(matrixRotation, matrixLookAt)
        viewMatrix.multiplication(matrixScale, matrixLookAtRotation)

        viewMatrix
    }
}
