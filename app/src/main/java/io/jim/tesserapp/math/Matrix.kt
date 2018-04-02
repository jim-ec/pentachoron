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
 * well, since they are homogeneous coordinates or Vectors as well.
 */
data class Matrix(val dimension: Int, private val rows: ArrayList<Vector> = ArrayList()) : Indexable<Vector> {

    init {
        // Initialize to identity matrix:
        assertTrue("Size must be > 0", dimension > 0)

        for (r in 0..dimension) rows.add(Vector(dimension + 1))

        identity()
    }

    /**
     * Copy constructor, creates a copy of [other].
     */
    constructor(other: Matrix) : this(other.dimension) {
        forEachCoefficient { r, c -> this[r][c] = other[r][c] }
    }

    override operator fun get(index: Int): Vector {
        return rows[index]
    }

    override operator fun set(index: Int, value: Vector) {
        for (i in 0 until value.dimension) this[index][i] = value[i]
    }

    var x: Vector by IndexAlias(0)
    var y: Vector by IndexAlias(1)
    var z: Vector by IndexAlias(2)
    var q: Vector by IndexAlias(3)

    var right: Vector by IndexAlias(0)
    var up: Vector by IndexAlias(1)
    var forward: Vector by IndexAlias(2)

    /**
     * Load the identity matrix.
     */
    fun identity() = this.apply {
        forEachCoefficient { r, c -> this[r][c] = if (r == c) 1.0 else 0.0 }
    }

    /**
     * Construct a matrix by defining a coordinate space from [axises].
     * The axises themselves are positioned at [base].
     */
    fun space(base: Vector, vararg axises: Vector) = this.apply {
        assertEquals("Axis count must match with matrix", axises.size, dimension)
        assertTrue("Axis dimension must match with matrix", axises.all { it.dimension == dimension })
        assertEquals("Base position dimension must match with matrix", base.dimension, dimension)

        forEachCoefficient(rows = dimension, cols = dimension) { r, c ->
            this[r][c] = axises[r][c]
        }
        forEachCoefficient(startRow = dimension, cols = dimension) { r, c ->
            this[r][c] = base[c]
        }
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
     * Construct a matrix, representing an affine rotation transformation of [theta] on the [a]-[b]-plane.
     * @exception AssertionError If any rotation-plane axis is larger in size than the matrix itself.
     */
    fun rotation(a: Int, b: Int, theta: Double) = this.apply {
        assertTrue("Plane-axis not in matrix dimension", a < dimension && b < dimension)
        // Rotation on y-q plane:
        //  -> y-axis rotates towards q-axis
        //  -> q-axis rotates towards negative y-axis
        // Myy = cos(theta)   | decreases
        // Myw = sin(theta)   | increases
        // Mwy = -sin(theta)  | increases, but in negative Vector, since y rotates towards q
        // Mww = cos(theta)   | decreases
        this[a][a] = cos(theta)
        this[a][b] = sin(theta)
        this[b][a] = -sin(theta)
        this[b][b] = cos(theta)
    }

    /**
     * Construct a matrix, representing an affine translation transformation by a given [v] vector.
     * Remember that vectors multiplied to this matrix must be homogeneous, their last component
     * determines whether they are transformed at all.
     */
    fun translation(v: Vector) = this.apply {
        assertEquals("Translation vector dimension must match with matrix", dimension, v.dimension)
        v.forEachIndexed { index, d ->
            this[dimension][index] = d
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
     * @param near Near plane. If Vector lies on that plane (negated), it will be projected to 0.
     * @param far Far plane. If Vector lies on that plane (negated), it will be projected to 1.
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
     * Multiply two matrices, [lhs] * [rhs], and store the result in this matrix,
     * without allocating new matrices.
     */
    fun multiplicationFrom(lhs: Matrix, rhs: Matrix) = this.apply {
        assertTrue("Matrix dimensions must match", dimension == lhs.dimension && lhs.dimension == rhs.dimension)
        forEachCoefficient { r, c ->
            this[r][c] = (0..dimension).map { i -> lhs[r][i] * rhs[i][c] }.sum()
        }
    }

    /**
     * Construct a look-at matrix, representing both, translation and rotation.
     * The camera is constructed in such a way that it is positioned at [eye], Vectors to [target],
     * and the upper edge is oriented in the [refUp] Vector.
     */
    fun lookAt(eye: Vector, target: Vector, refUp: Vector) = this.apply {
        assertTrue("Look at does only work in 3D", dimension == 3 && eye.dimension == 3 && target.dimension == 3)

        forward = (eye - target).normalize()
        right = (refUp cross forward).normalize()
        up = (forward cross right).normalize()

        transpose()

        this[3] = -eye applyPoint this
    }

    /**
     * Transpose this matrix.
     */
    fun transpose() = this.apply {
        var tmp: Double

        for (r in 0..dimension) {
            for (c in 0..r) {
                tmp = this[r][c]
                this[r][c] = this[c][r]
                this[c][r] = tmp
            }
        }
    }

    /**
     * Call a function for each coefficient. Indices of row and column are passed to [f].
     */
    fun forEachCoefficient(
            startRow: Int = 0, rows: Int = dimension + 1 - startRow,
            startCol: Int = 0, cols: Int = dimension + 1 - startCol,
            f: (r: Int, c: Int) -> Unit) {
        for (r in startRow until startRow + rows) {
            for (c in startCol until startCol + cols) {
                f(r, c)
            }
        }
    }

    /**
     * Allocate a float array, containing this matrix in column-major representation.
     */
    fun toFloatArray() = FloatArray(16) { i -> this[i / 4][i % 4].toFloat() }

    /**
     * Create a string representing this matrix.
     */
    override fun toString() =
            StringBuilder().also {
                it.append("[ (").append(dimension).append("D): ")
                rows.forEachIndexed { r, row ->
                    row.forEachIndexed { c, col ->
                        it.append(Format.decimalFormat.format(col))
                        if (c < dimension) it.append(", ")
                    }
                    if (r < dimension) it.append(" | ")
                }
                it.append(" ]")
            }.toString()

}
