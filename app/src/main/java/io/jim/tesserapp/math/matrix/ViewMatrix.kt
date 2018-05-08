package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.graphics.Camera
import io.jim.tesserapp.math.Vector

class ViewMatrix(
        private val camera: Camera
) : Matrix(4) {

    private val matrixLookAtRotation = Matrix(4)
    private val matrixRotation = Matrix(4)
    private val matrixHorizontalRotation = Matrix(4)
    private val matrixVerticalRotation = Matrix(4)
    private val matrixLookAt = Matrix(4)
    private val matrixScale = Matrix(4)

    private val eye = Vector(1f, 0f, 0f, 1f)

    companion object {
        val upVector = Vector(0f, 1f, 0f, 0f)
        val target = Vector(0f, 0f, 0f, 1f)
    }

    fun compute() {
        matrixHorizontalRotation.rotation(2, 0, camera.horizontalRotation)
        matrixVerticalRotation.rotation(0, 1, camera.verticalRotation)
        matrixRotation.multiplication(matrixHorizontalRotation, matrixVerticalRotation)

        eye.x = camera.distance

        matrixLookAt.lookAt(eye, target, upVector)
        matrixScale.scale(1f, camera.aspectRatio, 1f)

        matrixLookAtRotation.multiplication(matrixRotation, matrixLookAt)
        multiplication(matrixScale, matrixLookAtRotation)
    }

}
