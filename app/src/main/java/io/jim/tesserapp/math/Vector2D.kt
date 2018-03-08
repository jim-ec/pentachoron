package io.jim.tesserapp.math

class Vector2D(x: Double, y: Double)
    : Vector(doubleArrayOf(x, y)) {

    val x get() = this[0]
    val y get() = this[1]


    override val orthographicProjection
        get() = Vector1D(x)

}
