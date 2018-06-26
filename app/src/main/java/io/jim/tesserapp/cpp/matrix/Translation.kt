package io.jim.tesserapp.cpp.matrix

import io.jim.tesserapp.cpp.vector.VectorN
import io.jim.tesserapp.math.MathException

fun translation(size: Int, v: VectorN) =
        if (v.dimension != size - 1)
            throw MathException("")
        else
            quadratic(size) { row, col ->
                when (row) {
                    col -> 1.0
                    size - 1 -> v[col]
                    else -> 0.0
                }
            }
