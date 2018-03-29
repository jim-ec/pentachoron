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

    /**
     * Construct a matrix by defining a coordinate space from [axises].
     * The axises themselves are positioned at [base].
     */
    fun space(base: Point, vararg axises: Vector) = this.apply {
        assertEquals("Axis count must match with matrix", axises.size, dimension)
        assertTrue("Axis dimension must match with matrix", axises.all { it.dimension == dimension })
        assertEquals("Base position dimension must match with matrix", base.dimension, dimension)
        axises.forEachIndexed { r, axis ->
            axis.forEachIndexed { c, coefficient ->
                this[r][c] = coefficient
            }
        }
        base.forEachIndexed { index, d -> this[dimension][index] = d }
    }

    /**
     * Construct a matrix, representing an affine linear scaling transformation.
     */
    fun scale(scale: Double) = this.apply {
        for (i in 0 until dimension) {
            this[i][i] = scale
        }
    }

    /**
     * Construct a matrix, representing an affine linear scaling transformation.
     */
    fun scale(scale: Vector) = this.apply {
        assertEquals("Scale vector dimension must match with matrix", dimension, scale.dimension)
        scale.forEachIndexed { i, fi -> this[i][i] = fi }
    }

    /**
     * Construct a matrix, representing an affine rotation transformation of [phi] on the [a]-[b]-plane.
     * @exception AssertionError If any rotation-plane axis is larger in size than the matrix itself.
     */
    fun rotation(a: Int, b: Int, phi: Double) = this.apply {
        assertTrue("Plane-axis not in matrix dimension", a < dimension && b < dimension)
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
     * Construct a matrix, representing an affine translation transformation by a given [direction] vector.
     * Remember that vectors multiplied to this matrix must be homogeneous, their last component
     * determines whether they are transformed at all.
     */
    fun translation(direction: Direction) = this.apply {
        assertEquals("Translation vector dimension must match with matrix", dimension, direction.dimension)
        direction.forEachIndexed { i, d ->
            this[dimension][i] = d
        }
    }

    /**
     * Construct a matrix, representing an perspective division transformation.
     */
    fun perspective() = this.apply {
        this[dimension - 1][dimension] = -1.0
        this[dimension][dimension] = 0.0
    }

    /**
     * Construct a matrix, representing an perspective division transformation,
     * while remapping the last vector component between a near and far value.
     * @param near Near plane. If point lies on that plane (negated), it will be projected to 0.
     * @param far Far plane. If point lies on that plane (negated), it will be projected to 1.
     */
    fun perspective(near: Double, far: Double) = this.apply {
        perspective()
        assertTrue(near > 0.0)
        assertTrue(far > 0.0)
        assertTrue(far > near)
        this[dimension - 1][dimension - 1] = -far / (far - near)
        this[dimension][dimension - 1] = -(far * near) / (far - near)
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

    fun lookAt(from: Point, to: Point) {
        assertTrue("Look at does only work in 3D", dimension == 3 && from.dimension == 3 && to.dimension == 3)

        val ref = Direction(0.0, 1.0, 0.0)

        val forward = (from - to).apply { normalize() }
        val right = ref cross forward
        val up = forward cross right

        assertTrue("Forward cannot be equal to $ref", ref != forward)

        this[0][0] = right.x
        this[0][1] = right.y
        this[0][2] = right.z
        this[1][0] = up.x
        this[1][1] = up.y
        this[1][2] = up.z
        this[2][0] = forward.x
        this[2][1] = forward.y
        this[2][2] = forward.z
        this[3][0] = from.x
        this[3][1] = from.y
        this[3][2] = from.z

        TODO("Invert matrix, since that is cam-to-world")
    }

    /**
     * Construct a matrix representing a transpose of this matrix.
     */
    fun transposed() =
            Matrix(dimension).also { result ->
                forEachCoefficient { r, c -> result[r][c] = this[c][r] }
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
