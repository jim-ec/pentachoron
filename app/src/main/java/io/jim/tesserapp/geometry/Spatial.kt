package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.Matrix

open class Spatial(dimension: Int) {

    protected val matrix = Matrix(dimension)
    private val rotationZX = Matrix(dimension)
    private val rotationYX = Matrix(dimension)
    private val children = ArrayList<Spatial>()

    fun modelMatrix() = matrix.apply {
        multiplicationFrom(rotationZX, rotationYX)
    }

    fun rotationZX(theta: Double) {
        rotationZX.rotation(2, 0, theta)
    }

    fun rotationYX(theta: Double) {
        rotationYX.rotation(1, 0, theta)
    }

}
