package io.jim.tesserapp.math.matrix

/**
 * Projection matrix, projecting 3D into 2D.
 *
 * @param near Near plane.
 * @param far Far plane.
 */
class Projection3dMatrix(
        near: Float,
        far: Float
) : Matrix(4) {

    init {
        perspective2D(near, far)
    }

}
