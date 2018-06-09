package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.Vector3d

/**
 * Represent a `4x4` view matrix, including a look-at based transform and orbital rotation.
 */
class ViewMatrix : Matrix(4) {

    private val matrixLookAtRotation = Matrix(4)
    private val matrixRotation = Matrix(4)
    private val matrixHorizontalRotation = Matrix(4)
    private val matrixVerticalRotation = Matrix(4)
    private val matrixLookAt = LookAtMatrix()
    private val matrixScale = Matrix(4)

    private val eye = Vector3d(1.0, 0.0, 0.0)
    private val scale = Vector3d(1.0, 1.0, 1.0)

    companion object {
        private val upVector = Vector3d(0.0, 1.0, 0.0)
        private val target = Vector3d(0.0, 0.0, 0.0)
    }

    /**
     * Recomputes view matrix.
     */
    fun compute(
            distance: Double,
            aspectRatio: Double,
            horizontalRotation: Double,
            verticalRotation: Double
    ) {
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
        multiplication(matrixScale, matrixLookAtRotation)
    }

}
