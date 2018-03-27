package io.jim.tesserapp.geometry

import io.jim.tesserapp.gui.Color
import io.jim.tesserapp.math.Direction
import io.jim.tesserapp.math.Point

open class Geometry(val color: Color) {

    val points = ArrayList<Point>()
    val lines = ArrayList<Pair<Int, Int>>()

    fun addPoints(vararg p: Point) {
        points.addAll(p)
    }

    fun addLine(a: Int, b: Int) {
        lines.add(Pair(a, b))
    }

    fun toLineList() = ArrayList<Point>().apply {
        for (line in lines) {
            add(points[line.first])
            add(points[line.second])
        }
    }

    /**
     * Extrudes the whole geometry in the given [direction].
     * This works by duplicating the whole geometry and then connecting all point duplicate counterparts.
     */
    fun extrude(direction: Direction) {
        val size = points.size
        points.addAll(points.map { it + direction })
        lines.addAll(lines.map { Pair(it.first + size, it.second + size) })
        for (i in 0 until size) {
            addLine(i, i + size)
        }
    }

}
