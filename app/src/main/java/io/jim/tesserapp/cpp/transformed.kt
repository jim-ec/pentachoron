package io.jim.tesserapp.cpp

import io.jim.tesserapp.cpp.vector.VectorN
import io.jim.tesserapp.geometry.Geometry
import io.jim.tesserapp.geometry.Line

inline fun Geometry.transformed(crossinline visualizer: FourthDimensionVisualizer) = lines.map {
    val modelMatrix = onTransformUpdate()
    
    val transform = { point: VectorN ->
        (point * modelMatrix).let { if (isFourDimensional) visualizer(it) else it }
    }
    
    Line(
            transform(it.start),
            transform(it.end),
            it.color
    )
}
