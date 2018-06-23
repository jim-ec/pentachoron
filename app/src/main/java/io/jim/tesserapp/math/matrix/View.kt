package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.VectorN

/**
 * Return a view matrix.
 *
 * @param distance Camera distance from origin along the x-axis.
 * @param aspectRatio Aspect ratio, i.e. width over height.
 */
fun view(
        distance: Double,
        aspectRatio: Double,
        horizontalRotation: Double,
        verticalRotation: Double
) = rotation(4, RotationPlane.XZ, horizontalRotation) *
        rotation(4, RotationPlane.XY, verticalRotation) *
        lookAt(distance, refUp = VectorN(0.0, 1.0, 0.0)) *
        aspectRatioCorrection(aspectRatio)

/**
 * Corrects image distortion due to non-quadratic viewport.
 * Scaling happens in such a way that a square with a side length of 2 always fits into the viewport.
 * I.e. a wide viewport causes horizontal down-scaling, while a tall viewport causes vertical down-scaling.
 */
fun aspectRatioCorrection(aspectRatio: Double) =
        Matrix.scale(4,
                if (aspectRatio > 1)
                    VectorN(1 / aspectRatio, 1.0, 1.0)
                else
                    VectorN(1.0, aspectRatio, 1.0)
        )

/**
 * Return a look-at matrix.
 * @param distance Position of eye along the x-axis.
 * @param refUp Up direction of camera.
 */
fun lookAt(distance: Double, refUp: VectorN) = run {
    
    val forward = VectorN(1.0, 0.0, 0.0)
    val right = refUp.normalized() cross forward
    val up = forward cross right
    
    Matrix.translation(4, VectorN(-distance, 0.0, 0.0)) * Matrix(4, mapOf(
            0 to 0 to right.x,
            0 to 1 to right.y,
            0 to 2 to right.z,
            1 to 0 to up.x,
            1 to 1 to up.y,
            1 to 2 to up.z,
            2 to 0 to forward.x,
            2 to 1 to forward.y,
            2 to 2 to forward.z
    )).transposed()
}
