package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.vector.VectorN

fun extruded(
        positions: List<Line>,
        direction: VectorN,
        keepColors: Boolean = false,
        connectorColor: SymbolicColor = SymbolicColor.PRIMARY
): List<Line> {
    
    val duplicate = positions.map {
        Line(
                it.start + direction,
                it.end + direction,
                if (keepColors) it.color else SymbolicColor.PRIMARY
        )
    }
    
    val connections = positions.flatMap {
        listOf(
                Line(
                        it.start,
                        it.start + direction,
                        connectorColor
                ),
                Line(
                        it.end,
                        it.end + direction,
                        connectorColor
                )
        )
    }
    
    return positions + duplicate + connections
    
}
