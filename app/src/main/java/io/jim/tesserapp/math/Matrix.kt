package io.jim.tesserapp.math

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlin.math.cos
import kotlin.math.sin

class Matrix : ArrayList<Vector> {

    /**
     * Construct an identity matrix with side length equal to [size].
     */
    constructor(size: Int) {
        assertTrue("Size must be greater than 0", size > 0)
        for (i in 0 until size) {
            add(Vector(size).also { it[i] = 1.0 })
        }
    }

    /**
     * Construct a matrix, filled up with initial values.
     * @exception AssertionError If count of given values in [l] does not match up with the matrix [size].
     */
    constructor(size: Int, l: List<Double>) : this(size) {
        assertEquals(size * size, l.size)
        l.forEachIndexed { i, v -> this[i / size][i % size] = v }
    }

    companion object {

        fun space(dimension: Int, axises: List<Vector>) =
                Matrix(dimension).apply {
                    assertEquals("Axis count ${axises.size} does not match up with matrix size $dimension", axises.size + 1, dimension)
                    assertTrue("All vectors must have one component less than the matrix size $dimension", axises.all { it.dimension + 1 == dimension })
                    axises.forEachIndexed { r, axis ->
                        axis.forEachIndexed { c, coefficient ->
                            this[r][c] = coefficient
                        }
                    }
                }

        /**
         * Construct a matrix, representing an affine linear scaling transformation.
         */
        fun scale(dimension: Int, factor: Double) =
                Matrix(dimension).apply {
                    for (i in 0 until dimension - 1) {
                        this[i][i] = factor
                    }
                }

        /**
         * Construct a matrix, representing an affine linear scaling transformation.
         */
        fun scale(dimension: Int, factor: Vector) =
                Matrix(dimension).apply {

                    assertEquals("Scale vector dimension ${factor.dimension} does not match up with matrix dimension $dimension", dimension, factor.dimension + 1)

                    factor.forEachIndexed { i, fi -> this[i][i] = fi }
                }

        /**
         * Construct a matrix, representing an affine rotation transformation of [phi] on the [n]-[m]-plane.
         * @exception AssertionError If any rotation-plane axis is larger in size than the matrix itself.
         */
        fun rotation(dimension: Int, n: Int, m: Int, phi: Double) =
                Matrix(dimension).apply {
                    assertTrue("Plane-axis $n not in size $dimension", n < dimension)
                    assertTrue("Plane-axis $m not in size $dimension", m < dimension)
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
                Matrix(dimension).apply {
                    assertTrue("Plane-axis $n not in size $dimension", n < dimension)
                    assertTrue("Plane-axis $m not in size $dimension", m < dimension)
                    this[n][m] = nm
                    this[m][n] = mn
                }

        /**
         * Construct a matrix, representing an affine translation transformation by a given [t] vector.
         * Remember that vectors multiplied to this matrix must be homogeneous, their last component
         * determines whether they are transformed at all.
         * @exception AssertionError If [t]'s size is not equal to [size] less one.
         */
        fun translation(dimension: Int, t: Vector) =
                Matrix(dimension).apply {
                    assertEquals("Translation size ${t.dimension} does not match up with matrix size $dimension", dimension, t.dimension + 1)
                    t.forEachIndexed { i, ti ->
                        this[dimension - 1][i] = ti
                    }
                }

        /**
         * Construct a matrix, representing an perspective division transformation.
         * @param dimension Homogeneous dimension, e.g. 4 for a 3D perspective world.
         */
        fun perspective(dimension: Int) =
                Matrix(dimension).apply {
                    this[dimension - 2][dimension - 1] = -1.0
                    this[dimension - 1][dimension - 1] = 0.0
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
                    this[dimension - 2][dimension - 2] = -far / (far - near)
                    this[dimension - 1][dimension - 2] = -(far * near) / (far - near)
                }
    }

    infix fun compatible(rhs: Matrix) = size == rhs.size
    infix fun compatible(rhs: Vector) = size == rhs.dimension

    /**
     * Multiply this and a given right-hand-side matrix, resulting into a matrix.
     * @exception AssertionError If matrices are not of the same size.
     */
    operator fun times(rhs: Matrix) =
            Matrix(size).also {
                assertTrue(this compatible rhs)
                forEachCoefficient { r, c ->
                    it[r][c] = (0 until size).map { i -> this[r][i] * rhs[i][c] }.sum()
                }
            }

    /**
     * Construct a matrix representing a transpose of this matrix.
     */
    fun transposed() =
            Matrix(size).also {
                forEachCoefficient { r, c -> it[r][c] = this[c][r] }
            }

    /**
     * Call a function for each coefficient. Indices of row and column are passed to [f].
     */
    fun forEachCoefficient(f: (Int, Int) -> Unit) {
        forEachIndexed { r, row ->
            row.forEachIndexed { c, _ ->
                f(r, c)
            }
        }
    }

    override fun toString() =
            StringBuilder().also {
                it.append("[ (").append(size).append('x').append(size).append("): ")
                forEachIndexed { r, row ->
                    row.forEachIndexed { c, col ->
                        it.append(col)
                        if (c < size - 1) it.append(", ")
                    }
                    if (r < size - 1) it.append(" | ")
                }
                it.append(" ]")
            }.toString()

}
