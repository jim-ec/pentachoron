package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.Matrix

open class Spatial(dimension: Int) : Iterable<Spatial> {

    val matrix = Matrix(dimension)
    private val children = ArrayList<Spatial>()

    fun addChild(child: Spatial) {
        children.add(child)
    }

    override fun iterator() = children.iterator()

}
