package io.jim.tesserapp.cpp

import io.jim.tesserapp.cpp.matrix.Matrix
import io.jim.tesserapp.cpp.vector.VectorN
import io.jim.tesserapp.geometry.Line
import java.nio.DoubleBuffer

inline fun transformed(
        modelMatrix: Matrix,
        isFourDimensional: Boolean,
        positions: DoubleBuffer,
        crossinline visualizer: FourthDimensionVisualizer
): List<Line> {
    
    return (0 until positions.limit() step 8).map { index ->
        val transform = { point: VectorN ->
            (point * modelMatrix).let { if (isFourDimensional) visualizer(it) else it }
        }
        
        Line(
                transform(VectorN(positions[index], positions[index + 1], positions[index + 2], positions[index + 3])),
                transform(VectorN(positions[index + 4], positions[index + 5], positions[index + 6], positions[index + 7]))
        )
    }
    
}
