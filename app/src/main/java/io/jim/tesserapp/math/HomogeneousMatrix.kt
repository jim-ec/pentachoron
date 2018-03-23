package io.jim.tesserapp.math

import kotlin.math.cos
import kotlin.math.sin

/**
 * @constructor Construct the identity matrix.
 */
class HomogeneousMatrix(val dimension: Int) : Matrix(dimension + 1) {

    init {
        assert(dimension > 0) { "Dimension must be greater than 0" }
        for (i in 0 until dimension) {
            this[i][i] = 1.0
        }
    }

    companion object {

        fun space(dimension: Int, axises: List<Vector>) =
                HomogeneousMatrix(dimension).apply {
                    assert(axises.size == dimension) { "Axis count ${axises.size} does not match up with matrix dimension $dimension" }
                    assert(axises.all { it.dimension == dimension }) { "All vectors must have one component less than the matrix dimension $dimension" }
                    axises.forEachIndexed { r, axis ->
                        for (c in 0 until dimension) {
                            this[r][c] = axis[c]
                        }
                    }
                }

        /**
         * Construct a matrix, representing an affine linear scaling transformation.
         */
        fun scale(dimension: Int, factor: Double) =
                HomogeneousMatrix(dimension).apply {
                    for (i in 0 until dimension) {
                        this[i][i] = factor
                    }
                }

        /**
         * Construct a matrix, representing an affine rotation transformation of [phi] on the [n]-[m]-plane.
         * @exception AssertionError If any rotation-plane axis is larger in dimension than the matrix itself.
         */
        fun rotation(dimension: Int, n: Int, m: Int, phi: Double) =
                HomogeneousMatrix(dimension).apply {
                    assert(n <= dimension) { "Plane-axis $n not in dimension $dimension" }
                    assert(m <= dimension) { "Plane-axis $m not in dimension $dimension" }
                    // Rotation on y-w plane:
                    //  -> y-axis rotates towards w-axis
                    //  -> w-axis rotates towards negative y-axis
                    // Myy = cos(phi)   | decreases
                    // Myw = sin(phi)   | increases
                    // Mwy = -sin(phi)  | increases, but in negative direction, since y rotates towards w
                    // Mww = cos(phi)   | decreases
                    this[n][n] = cos(phi)
                    this[n][m] = sin(phi)
                    this[m][n] = -sin(phi)
                    this[m][m] = cos(phi)
                }

        /**
         * Construct a matrix, representing an affine shearing transformation on the [n]-[m]-plane.
         * The [n]-axis stretched along the [m]-axis by [nm],
         * and the [m]-axis is stretched along the [n]-axis by [mn].
         */
        fun shear(dimension: Int, n: Int, nm: Double, m: Int, mn: Double) =
                HomogeneousMatrix(dimension).apply {
                    assert(n <= dimension) { "Axis $n not in dimension $dimension" }
                    assert(m <= dimension) { "Axis $m not in dimension $dimension" }
                    this[n][m] = nm
                    this[m][n] = mn
                }

        /**
         * Construct a matrix, representing an affine translation transformation by a given [v] vector.
         * Remember that vectors multiplied to this matrix must be homogeneous, their last component
         * determines whether they are transformed at all.
         * @exception AssertionError If [v]'s dimension is not equal to [size] less one.
         */
        fun translation(dimension: Int, v: Vector) =
                HomogeneousMatrix(dimension).apply {
                    assert(dimension == v.dimension) { "Translation dimension ${v.dimension} does not match up with matrix dimension $dimension" }
                    for (i in 0 until dimension) {
                        this[dimension][i] = v[i]
                    }
                }

    }

}
