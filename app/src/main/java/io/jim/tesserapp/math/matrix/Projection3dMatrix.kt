package io.jim.tesserapp.math.matrix

/**
 * Projection matrix without z-remapping.
 */
fun projection3dMatrix() = Matrix(4).apply {
    perspective2D()
}


/**
 * Projection matrix with z-remapping.
 * @param near Near plane.
 * @param far Far plane.
 */
fun projection3dMatrix(near: Double, far: Double) = Matrix(4).apply {
    perspective2D(near, far)
}
