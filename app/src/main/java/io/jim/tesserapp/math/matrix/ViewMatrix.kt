package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.VectorN

/**
 * Compute the view matrix.
 *
 * @param distance Camera distance from origin.
 * @param aspectRatio Aspect ratio, i.e. width over height.
 *
 * @return Computed view matrix. Returned instance is the same for each call.
 */
fun viewMatrix(
        distance: Double,
        aspectRatio: Double,
        horizontalRotation: Double,
        verticalRotation: Double
) = rotation(4, RotationPlane.XZ, horizontalRotation) *
        rotation(4, RotationPlane.XY, verticalRotation) *
        lookAt(distance, refUp = VectorN(0.0, 1.0, 0.0)) *
        Matrix.scale(4, if (aspectRatio > 1) {
            // Viewport is wide => shrink horizontally
            VectorN(1 / aspectRatio, 1.0, 1.0)
        } else {
            // Viewport is tall => shrink vertically
            VectorN(1.0, aspectRatio, 1.0)
        })
