package io.jim.tesserapp.math.transform

import io.jim.tesserapp.graphics.Camera
import io.jim.tesserapp.math.vector.Vector3d

class ViewMatrix(
        private val camera: Camera
) : Matrix(4) {

    private val matrixLookAtRotation = Matrix(4)
    private val matrixRotation = Matrix(4)
    private val matrixHorizontalRotation = Matrix(4)
    private val matrixVerticalRotation = Matrix(4)
    private val matrixLookAt = LookAtMatrix()
    private val matrixScale = Matrix(4)

    private val eye = Vector3d(1f, 0f, 0f)

    companion object {
        val upVector = Vector3d(0f, 1f, 0f)
        val target = Vector3d(0f, 0f, 0f)
    }

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
        matrixScale.scale(Vector3d(1f, camera.aspectRatio, 1f))

        matrixLookAtRotation.multiplication(matrixRotation, matrixLookAt)
        multiplication(matrixScale, matrixLookAtRotation)
    }

}
