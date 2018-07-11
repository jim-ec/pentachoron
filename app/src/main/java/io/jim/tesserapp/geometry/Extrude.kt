package io.jim.tesserapp.geometry

fun extruded(
        positions: List<Line>,
        direction: Position
): List<Line> {
    
    val duplicate = positions.map {
        Line(
                it.start + direction,
                it.end + direction
        )
    }
    
    val connections = positions.flatMap {
        listOf(
                Line(it.start, it.start + direction),
                Line(it.end, it.end + direction)
        )
    }
    
    return positions + duplicate + connections
    
}
