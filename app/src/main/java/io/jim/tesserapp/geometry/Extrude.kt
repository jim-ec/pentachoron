package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

fun extruded(
        positions: List<Line>,
        direction: VectorN,
        keepColors: Boolean = false,
        connectorColor: Geometry.Color = Geometry.Color.PRIMARY
): List<Line> {
    
    val duplicate = positions.map {
        Line(
                it.start + direction,
                it.end + direction,
                if (keepColors) it.color else Geometry.Color.PRIMARY
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
