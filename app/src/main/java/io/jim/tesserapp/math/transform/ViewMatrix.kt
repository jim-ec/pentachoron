package io.jim.tesserapp.math.transform

import io.jim.tesserapp.graphics.Camera
import io.jim.tesserapp.math.vector.Vector3d

/**
 * Represent a `4x4` view matrix, including a look-at based transform, screen ration as well as
 * orbital rotation.
 *
 * The exact parameters defining each transform are taken from the [camera].
 * When these parameters change, this view matrix is not recomputed automatically, but rather
 * through a call to [compute].
 */
class ViewMatrix(
        private val camera: Camera
) : Matrix(4) {

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
    fun compute() {
        matrixHorizontalRotation.rotation(2, 0, camera.horizontalRotation)
        matrixVerticalRotation.rotation(0, 1, camera.verticalRotation)
        matrixRotation.multiplication(matrixHorizontalRotation, matrixVerticalRotation)

        eye.x = camera.distance

        matrixLookAt.lookAt(
                eye = eye,
                target = target,
                refUp = upVector
        )

        scale.y = camera.aspectRatio
        matrixScale.scale(scale)

        matrixLookAtRotation.multiplication(matrixRotation, matrixLookAt)
        multiplication(matrixScale, matrixLookAtRotation)
    }

}
