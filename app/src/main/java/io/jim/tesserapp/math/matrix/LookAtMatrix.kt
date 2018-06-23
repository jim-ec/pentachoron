package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.VectorN

/**
 * Compute view matrix.
 * @param distance Position of eye along the x-axis.
 * @param refUp Up direction of camera.
 */
fun lookAt(distance: Double, refUp: VectorN): Matrix {
    
    val forward = VectorN(1.0, 0.0, 0.0)
    val right = refUp.normalized() cross forward
    val up = forward cross right
    
    val matrix = Matrix(4)
    
    matrix[0, 0] = right.x
    matrix[0, 1] = right.y
    matrix[0, 2] = right.z
    
    matrix[1, 0] = up.x
    matrix[1, 1] = up.y
    matrix[1, 2] = up.z
    
    matrix[2, 0] = forward.x
    matrix[2, 1] = forward.y
    matrix[2, 2] = forward.z
    
    return Matrix.translation(4, VectorN(-distance, 0.0, 0.0)) * matrix.transposed()
}
