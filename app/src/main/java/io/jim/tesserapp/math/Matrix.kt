package io.jim.tesserapp.math

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlin.math.cos
import kotlin.math.sin

/**
 * Homogeneous matrices used for affine and perspective transformation.
 * The [dimension] specifies the vector dimension this matrix can be applied to.
 * The matrix size itself is always one dimension bigger than it specifies,
 * as the matrix is used for homogeneous coordinates.
 * Vectors multiplied to this matrix must be one greater than the dimension as
 * well, since they are homogeneous coordinates or directions as well.
 */
class Matrix(val dimension: Int) {

    private val coefficients = ArrayList<ArrayList<Double>>()

    init {
        // Initialize to identity matrix:
        assertTrue("Size must be > 0", dimension > 0)
        for (i in 0..dimension) coefficients.add(ArrayList<Double>().also {
            for (j in 0..dimension) it.add(0.0)
            it[i] = 1.0
        })
    }

    operator fun get(index: Int): ArrayList<Double> {
        return coefficients[index]
    }

    companion object {

        /**
         * Construct an [n] dimensional matrix by defining a coordinate space from [axises].
         * The axises themselves are positioned at [base].
         */
        fun space(n: Int, base: Point, vararg axises: Vector) =
                Matrix(n).apply {
                    assertEquals("Axis count must match with matrix", axises.size, n)
                    assertTrue("Axis dimension must match with matrix", axises.all { it.dimension == n })
                    assertEquals("Base position dimension must match with matrix", base.dimension, dimension)
                    axises.forEachIndexed { r, axis ->
                        axis.forEachIndexed { c, coefficient ->
                            this[r][c] = coefficient
                        }
                    }
                    base.forEachIndexed { index, d -> this[dimension][index] = d }
                }

        /**
         * Construct an [n] dimensional matrix, representing an affine linear scaling transformation.
         */
        fun scale(n: Int, scale: Double) =
                Matrix(n).apply {
                    for (i in 0 until n) {
                        this[i][i] = scale
                    }
                }

        /**
         * Construct a matrix, representing an affine linear scaling transformation.
         */
        fun scale(n: Int, scale: Vector) =
                Matrix(n).apply {
                    assertEquals("Scale vector dimension must match with matrix", dimension, scale.dimension)
                    scale.forEachIndexed { i, fi -> this[i][i] = fi }
                }

        /**
         * Construct an [n] dimensional matrix, representing an affine rotation transformation of [phi] on the [a]-[b]-plane.
         * @exception AssertionError If any rotation-plane axis is larger in size than the matrix itself.
         */
        fun rotation(n: Int, a: Int, b: Int, phi: Double) =
                Matrix(n).apply {
                    assertTrue("Plane-axis not in matrix dimension", a < n && b < n)
                    // Rotation on y-q plane:
                    //  -> y-axis rotates towards q-axis
                    //  -> q-axis rotates towards negative y-axis
                    // Myy = cos(phi)   | decreases
                    // Myw = sin(phi)   | increases
                    // Mwy = -sin(phi)  | increases, but in negative direction, since y rotates towards q
                    // Mww = cos(phi)   | decreases
                    this[a][a] = cos(phi)
                    this[a][b] = sin(phi)
                    this[b][a] = -sin(phi)
                    this[b][b] = cos(phi)
                }

        /**
         * Construct a matrix, representing an affine translation transformation by a given [t] vector.
         * Remember that vectors multiplied to this matrix must be homogeneous, their last component
         * determines whether they are transformed at all.
         * @exception AssertionError If [t]'s size is not equal to [n] less one.
         */
        fun translation(n: Int, t: Vector) =
                Matrix(n).apply {
                    assertEquals("Translation vector dimension must match with matrix", n, t.dimension)
                    t.forEachIndexed { i, ti ->
                        this[n][i] = ti
                    }
                }

        /**
         * Construct a matrix, representing an perspective division transformation.
         */
        fun perspective(n: Int) =
                Matrix(n).apply {
                    this[dimension - 1][dimension] = -1.0
                    this[dimension][dimension] = 0.0
                }

        /**
         * Construct a matrix, representing an perspective division transformation,
         * while remapping the last vector component between a near and far value.
         * @param dimension Homogeneous dimension, e.g. 4 for a 3D perspective world.
         * @param near Near plane. If point lies on that plane (negated), it will be projected to 0.
         * @param far Far plane. If point lies on that plane (negated), it will be projected to 1.
         */
        fun perspective(dimension: Int, near: Double, far: Double) =
                perspective(dimension).apply {
                    assertTrue(near > 0.0)
                    assertTrue(far > 0.0)
                    assertTrue(far > near)
                    this[dimension - 1][dimension - 1] = -far / (far - near)
                    this[dimension][dimension - 1] = -(far * near) / (far - near)
                }
    }

    infix fun compatible(rhs: Matrix) = dimension == rhs.dimension
    infix fun compatible(rhs: Vector) = dimension == rhs.dimension

    /**
     * Multiply this and a given right-hand-side matrix, resulting into a matrix.
     */
    operator fun times(rhs: Matrix) =
            Matrix(dimension).also {
                assertTrue("Matrices must be compatible", this compatible rhs)
                forEachCoefficient { r, c ->
                    it[r][c] = (0..dimension).map { i -> this[r][i] * rhs[i][c] }.sum()
                }
            }

    /**
     * Construct a matrix representing a transpose of this matrix.
     */
    fun transposed() =
            Matrix(dimension).also {
                forEachCoefficient { r, c -> it[r][c] = this[c][r] }
            }

    /**
     * Call a function for each coefficient. Indices of row and column are passed to [f].
     */
    fun forEachCoefficient(f: (Int, Int) -> Unit) {
        coefficients.forEachIndexed { r, row ->
            row.forEachIndexed { c, _ ->
                f(r, c)
            }
        }
    }

    override fun toString() =
            StringBuilder().also {
                it.append("[ (").append(dimension).append("D): ")
                coefficients.forEachIndexed { r, row ->
                    row.forEachIndexed { c, col ->
                        it.append(col)
                        if (c < dimension) it.append(", ")
                    }
                    if (r < dimension) it.append(" | ")
                }
                it.append(" ]")
            }.toString()

}
