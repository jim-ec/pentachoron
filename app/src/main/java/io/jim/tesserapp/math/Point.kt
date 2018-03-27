package io.jim.tesserapp.math

import junit.framework.Assert

class Point(components: List<Double>) : Vector(components) {

    override val homogeneous: List<Double>
        get() = ArrayList<Double>(this.toList()).apply { add(1.0) }

    constructor(vararg components: Double) : this(components.toList())

    /**
     * Add a [direction] to this point.
     */
    operator fun plus(direction: Direction) =
            Point(zip(direction) { a, b -> a + b })

    /**
     * Subtract [direction] from this position.
     */
    operator fun minus(direction: Direction) =
            Point(zip(direction) { a, b -> a - b })

    /**
     * Returns a direction pointing from this to [point].
     */
    operator fun minus(point: Point) =
            Direction(zip(point) { a, b -> a - b })

    /**
     * Scales this point by [scale].
     */
    override operator fun times(scale: Double) =
            Point(map { it * scale })

    /**
     * Divides this vector through [divisor].
     */
    override operator fun div(divisor: Double) =
            Point(map { it / divisor })

    /**
     * Multiply this and a given left-hand-side vector, resulting into a vector.
     * Performs perspective division if necessary after matrix multiplication.
     * @exception AssertionError If matrix and vector are not of the same size.
     */
    override operator fun times(rhs: Matrix) =
            Point(ArrayList<Double>().also { list ->
                Assert.assertTrue("Vector and matrix must be compatible", rhs compatible this)

                for (c in 0..dimension) {
                    list.add((0..dimension).map { i -> rhs[i][c] * if (i < dimension) this[i] else 1.0 }.sum())
                }

                // Perspective division:
                if (list.last() != 0.0) {
                    list.forEachIndexed { index, d -> list[index] = d / list.last() }
                }
                list.removeAt(dimension)
            })

}