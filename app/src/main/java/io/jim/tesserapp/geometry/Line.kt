package io.jim.tesserapp.geometry

data class Line(
        val start: Position,
        val end: Position
) {
    
    val points = listOf(start, end)
    
}
