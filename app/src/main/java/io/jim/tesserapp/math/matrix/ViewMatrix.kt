package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.Vector3d

/**
 * Responsible for computing a view matrix.
 */
class ViewMatrix {

    private val viewMatrix = Matrix(4)
    private val matrixLookAtRotation = Matrix(4)
    private val matrixRotation = Matrix(4)
    private val matrixHorizontalRotation = Matrix(4)
    private val matrixVerticalRotation = Matrix(4)
    private val matrixLookAt = LookAtMatrix()
    private val matrixScale = Matrix(4)

    private val eye = Vector3d(1.0, 0.0, 0.0)
    private val scale = Vector3d(1.0, 1.0, 1.0)

    private val upVector = Vector3d(0.0, 1.0, 0.0)
    private val target = Vector3d(0.0, 0.0, 0.0)

    /**
     * Compute the view matrix.
     *
     * @param distance Camera distance from origin.
     * @param aspectRatio Aspect ratio, i.e. width over height.
     *
     * @return Computed view matrix. Returned instance is the same for each call.
     */
    operator fun invoke(
            distance: Double,
            aspectRatio: Double,
            horizontalRotation: Double,
            verticalRotation: Double
    ): Matrix {
        matrixHorizontalRotation.rotation(2, 0, horizontalRotation)
        matrixVerticalRotation.rotation(0, 1, verticalRotation)
        matrixRotation.multiplication(matrixHorizontalRotation, matrixVerticalRotation)

        eye.x = distance

        matrixLookAtRotation.multiplication(matrixRotation, matrixLookAt(eye, target, upVector))

        if (aspectRatio > 1) {
            // Viewport is wide => shrink horizontally
            scale.x = 1 / aspectRatio
        } else {
            // Viewport is tall => shrink vertically
            scale.y = aspectRatio
        }
        matrixScale.scale(scale)
        viewMatrix.multiplication(matrixLookAtRotation, matrixScale)

        return viewMatrix
    }

}
