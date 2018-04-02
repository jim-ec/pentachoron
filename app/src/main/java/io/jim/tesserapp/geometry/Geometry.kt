package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Vector
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue

open class Geometry(dimension: Int, val color: Color) : Spatial(dimension) {

    val points = ArrayList<Vector>()
    val lines = ArrayList<Pair<Int, Int>>()

    fun addPoints(vararg p: Vector) {
        assertTrue("Point must match geometry dimension", p.all { it.dimension == matrix.dimension })
        points.addAll(p)
    }

    fun addLine(a: Int, b: Int) {
        lines.add(Pair(a, b))
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate counterparts.
     */
    fun extrude(direction: Vector) {
        assertEquals("Extrude vector must match geometry dimension", matrix.dimension, direction.dimension)
        val size = points.size
        points.addAll(points.map { it + direction })
        lines.addAll(lines.map { Pair(it.first + size, it.second + size) })
        for (i in 0 until size) {
            addLine(i, i + size)
        }
    }

}
