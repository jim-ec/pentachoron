package io.jim.tesserapp.math.matrix

import io.jim.tesserapp.math.common.MathException
import io.jim.tesserapp.math.vector.VectorN

fun translation(size: Int, v: VectorN) =
        if (v.dimension != size - 1)
            throw MathException("")
        else
            Matrix(size) { row, col ->
                when (row) {
                    col -> 1.0
                    size - 1 -> v[col]
                    else -> 0.0
                }
            }
