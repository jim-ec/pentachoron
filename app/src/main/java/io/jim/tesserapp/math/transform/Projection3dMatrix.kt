package io.jim.tesserapp.math.transform

/**
 * Projection matrix, projecting 3D into 2D.
 */
class Projection3dMatrix : Matrix {

    /**
     * Projection matrix without z-remapping.
     */
    constructor() : super(4) {
        perspective2D()
    }

    /**
     * Projection matrix with z-remapping.
     * @param near Near plane.
     * @param far Far plane.
     */
    constructor(near: Double, far: Double) : super(4) {
        perspective2D(near, far)
    }

}
