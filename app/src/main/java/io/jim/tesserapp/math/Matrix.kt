package io.jim.tesserapp.math

import junit.framework.Assert.assertTrue
import kotlin.math.cos
import kotlin.math.sin

/**
 * 4 dimensional matrix.
 */
data class Matrix(private val rows: ArrayList<Vector> = ArrayList())
    : Indexable<Vector>, Iterable<Vector> {

    private val range = 0..3

    init {
        // Initialize to identity matrix:
        rows += Vector(1.0, 0.0, 0.0, 0.0)
        rows += Vector(0.0, 1.0, 0.0, 0.0)
        rows += Vector(0.0, 0.0, 1.0, 0.0)
        rows += Vector(0.0, 0.0, 0.0, 1.0)
    }

    /**
     * Return the row at the given [index]
     */
    override operator fun get(index: Int): Vector {
        return rows[index]
    }

    /**
     * Set the row at the given [index] to [value]
     */
    override operator fun set(index: Int, value: Vector) {
        for (i in 0 until 4) this[index][i] = value[i]
    }

    override fun iterator() = rows.iterator()

    /**
     * X row.
     */
    var x: Vector by IndexAlias(0)

    /**
     * Y row.
     */
    var y: Vector by IndexAlias(1)

    /**
     * Z row.
     */
    var z: Vector by IndexAlias(2)

    /**
     * W row.
     */
    var q: Vector by IndexAlias(3)

    /**
     * Right vector.
     */
    var right: Vector by IndexAlias(0)

    /**
     * Up vector.
     */
    var up: Vector by IndexAlias(1)

    /**
     * Forward vector.
     */
    var forward: Vector by IndexAlias(2)

    /**
     * Base vector.
     */
    var base: Vector by IndexAlias(3)

    /**
     * Construct a matrix by defining a coordinate space from [right], [up] and [forward].
     * The axises themselves are positioned at [base].
     */
    fun space(right: Vector, up: Vector, forward: Vector, base: Vector) = this.apply {
        this.right = right
        this.up = up
        this.forward = forward
        this.base = base
    }

    /**
     * Construct a matrix, representing an affine linear scaling transformation.
     */
    fun scale(scale: Vector) = this.apply {
        for (i in 0..3) {
            this[i][i] = scale[i]
        }
    }

    /**
     * Construct a matrix, representing an affine rotation transformation of [theta] on the [a]-[b]-plane.
     * @exception AssertionError If any rotation-plane axis is larger in size than the matrix itself.
     */
    fun rotation(a: Int, b: Int, theta: Double) = this.apply {
        assertTrue("Plane-axis not in matrix dimension", a <= 3 && b <= 3)
        // Rotation on y-w plane:
        //  -> y-axis rotates towards w-axis
        //  -> w-axis rotates towards negative y-axis
        // Myy = cos(theta)   | decreases
        // Myw = sin(theta)   | increases
        // Mwy = -sin(theta)  | increases, but in negative Vector, since y rotates towards w
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
        this.base = v
    }

    /**
     * Construct a matrix, representing an perspective division transformation.
     */
    fun perspective() = this.apply {
        this[2][3] = -1.0
        this[3][3] = 0.0
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
        this[2][2] = -far / (far - near)
        this[3][2] = -(far * near) / (far - near)
    }

    /**
     * Multiply two matrices, [lhs] * [rhs], and store the result in this matrix,
     * without allocating new matrices.
     */
    fun multiplicationFrom(lhs: Matrix, rhs: Matrix) = this.apply {
        forEachCoefficient { r, c ->
            var sum = 0.0
            for (i in range) {
                sum += lhs[r][i] * rhs[i][c]
            }
            this[r][c] = sum
        }
    }

    /**
     * Construct a look-at matrix, representing both, translation and rotation.
     * The camera is constructed in such a way that it is positioned at [eye], Vectors to [target],
     * and the upper edge is oriented in the [refUp] Vector.
     */
    fun lookAt(eye: Vector, target: Vector, refUp: Vector) = this.apply {

        forward = (eye - target).normalize()
        right = (refUp cross forward).normalize()
        up = (forward cross right).normalize()

        transpose()

        this[3] = Vector(-eye.x, -eye.y, -eye.z, eye.w) * this
    }

    /**
     * Transpose this matrix.
     */
    fun transpose() = this.apply {
        var tmp: Double

        for (r in range) {
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
            startRow: Int = 0, rows: Int = 4 - startRow,
            startCol: Int = 0, cols: Int = 4 - startCol,
            f: (r: Int, c: Int) -> Unit) {
        for (r in startRow until startRow + rows) {
            for (c in startCol until startCol + cols) {
                f(r, c)
            }
        }
    }

    /**
     * Store this matrix to a target float [array] at [offset] in row-major order.
     */
    fun storeToFloatArray(array: FloatArray, offset: Int) {
        var i = 0
        forEachCoefficient { r, c ->
            array[offset + i++] = this[r][c].toFloat()
        }
    }

    /**
     * Create a string representing this matrix.
     */
    override fun toString() =
            StringBuilder().also {
                it.append("[ ")
                rows.forEachIndexed { r, row ->
                    for (c in 0..3) {
                        val col = row[c]
                        it.append(decimalFormat.format(col))
                        if (c < 3) it.append(", ")
                    }
                    if (r < 3) it.append(" | ")
                }
                it.append(" ]")
            }.toString()

}
