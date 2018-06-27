package io.jim.tesserapp.cpp

import io.jim.tesserapp.cpp.matrix.Matrix
import io.jim.tesserapp.cpp.vector.VectorN
import io.jim.tesserapp.geometry.Line

inline fun transformed(
        modelMatrix: Matrix,
        isFourDimensional: Boolean,
        lines: List<Line>,
        crossinline visualizer: FourthDimensionVisualizer
) = lines.map {
    
    val transform = { point: VectorN ->
        (point * modelMatrix).let { if (isFourDimensional) visualizer(it) else it }
    }
    
    Line(
            transform(it.start),
            transform(it.end)
    )
}
