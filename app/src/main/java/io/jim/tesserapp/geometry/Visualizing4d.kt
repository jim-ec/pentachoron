package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

fun Line.projectWireframe() = run {
    val projectPoint = { pos: VectorN -> pos / pos.q }
    
    Line(projectPoint(start), projectPoint(end), color)
}

fun Line.collapseZ() = run {
    val projectPoint = { pos: VectorN -> with(pos) { VectorN(x, y, q, 0.0) } }
    
    Line(projectPoint(start), projectPoint(end), color)
}
