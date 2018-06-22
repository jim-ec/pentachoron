package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.vector.VectorN

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

    private val upVector = VectorN(0.0, 1.0, 0.0)

    /**
     * Compute the view matrix.
     *
     * @param distance Camera distance from origin.
     * @param aspectRatio Aspect ratio, i.e. width over height.
     *
     * @return Computed view matrix. Returned instance is the same for each call.
     */
    fun computed(
            distance: Double,
            aspectRatio: Double,
            horizontalRotation: Double,
            verticalRotation: Double
    ): Matrix {
        matrixHorizontalRotation.rotation(2, 0, horizontalRotation)
        matrixVerticalRotation.rotation(0, 1, verticalRotation)
        matrixRotation.multiplication(matrixHorizontalRotation, matrixVerticalRotation)

        matrixLookAtRotation.multiplication(matrixRotation, matrixLookAt.computed(distance, upVector))

        matrixScale.scale(
                if (aspectRatio > 1) {
                    // Viewport is wide => shrink horizontally
                    VectorN(1 / aspectRatio, 1.0, 1.0)
                } else {
                    // Viewport is tall => shrink vertically
                    VectorN(1.0, aspectRatio, 1.0)
                }
        )
        viewMatrix.multiplication(matrixLookAtRotation, matrixScale)
        
        return viewMatrix
    }
    
}
