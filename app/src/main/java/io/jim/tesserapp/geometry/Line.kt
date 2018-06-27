package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.vector.VectorN

data class Line(
        val start: VectorN,
        val end: VectorN
) {
    
    val points = listOf(start, end)
    
}
