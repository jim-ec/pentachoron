package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.VectorN

/**
 * Compute view matrix.
 * @param distance Position of eye along the x-axis.
 * @param refUp Up direction of camera.
 */
fun lookAt(distance: Double, refUp: VectorN) = run {
    
    val forward = VectorN(1.0, 0.0, 0.0)
    val right = refUp.normalized() cross forward
    val up = forward cross right
    
    Matrix.translation(4, VectorN(-distance, 0.0, 0.0)) * Matrix(4, 4, mapOf(
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
