package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.vector.VectorN

fun scale(size: Int, factors: VectorN) =
        if (factors.dimension != size - 1)
            throw MathException("")
        else
            quadratic(size) { row, col ->
                if (row == size - 1 && col == size - 1) 1.0
                else if (row == col) factors[row]
                else 0.0
            }
