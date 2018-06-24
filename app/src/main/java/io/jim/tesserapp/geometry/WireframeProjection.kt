package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

fun Line.projectWireframe(): Line {
    
    val projectPoint = { pos: VectorN -> pos / pos.q }
    
    return Line(projectPoint(start), projectPoint(end), color)
    
}
