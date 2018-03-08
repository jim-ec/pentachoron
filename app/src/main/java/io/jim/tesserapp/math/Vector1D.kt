package io.jim.tesserapp.math

class Vector1D(x: Double)
    : Vector(doubleArrayOf(x)) {

    val x get() = this[0]

}
