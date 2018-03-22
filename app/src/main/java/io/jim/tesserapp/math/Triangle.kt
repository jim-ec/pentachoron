package io.jim.tesserapp.math

class Triangle(a: Vector, b: Vector, c: Vector) {

    private val points: Array<Vector> = arrayOf(a, b, c)

    val first get() = points[0]
    val second get() = points[1]
    val third get() = points[2]

    fun iterator() = points.iterator()

}
