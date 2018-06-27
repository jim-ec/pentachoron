package io.jim.tesserapp.cpp.matrix

import io.jim.tesserapp.cpp.Camera
import io.jim.tesserapp.cpp.vector.VectorN

fun view(camera: Camera) = transformChain(
        rotation(4, RotationPlane.AROUND_Y, camera.horizontalRotation),
        rotation(4, RotationPlane.AROUND_Z, camera.verticalRotation),
        lookAt(camera.distance, refUp = VectorN(0.0, 1.0, 0.0)),
        aspectRatioCorrection(camera.aspectRatio))

/**
 * Corrects image distortion due to non-quadratic viewport.
 * Scaling happens in such a way that a square with a side length of 2 always fits into the viewport.
 * I.e. a wide viewport causes horizontal down-scaling, while a tall viewport causes vertical down-scaling.
 */
fun aspectRatioCorrection(aspectRatio: Double) =
        scale(4,
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
    
    translation(4, VectorN(-distance, 0.0, 0.0)) * identity(4, values = mapOf(
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
