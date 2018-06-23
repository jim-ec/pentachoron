package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

fun grid(): List<Line> =
        (-10..-1).flatMap { i ->
            (i / 2.0).let {
                listOf(
                        Line(VectorN(it, 0.0, -5.0, 0.0), VectorN(it, 0.0, 5.0, 0.0)),
                        Line(VectorN(-5.0, 0.0, it, 0.0), VectorN(5.0, 0.0, it, 0.0))
                )
            }
        } +
                (1..10).flatMap { i ->
                    (i / 2.0).let {
                        listOf(
                                Line(VectorN(it, 0.0, -5.0, 0.0), VectorN(it, 0.0, 5.0, 0.0)),
                                Line(VectorN(-5.0, 0.0, it, 0.0), VectorN(5.0, 0.0, it, 0.0))
                        )
                    }
                } +
                listOf(
                        Line(VectorN(-5.0, 0.0, 0.0, 0.0), VectorN(0.0, 0.0, 0.0, 0.0)),
                        Line(VectorN(1.0, 0.0, 0.0, 0.0), VectorN(5.0, 0.0, 0.0, 0.0)),
                        Line(VectorN(0.0, 0.0, -5.0, 0.0), VectorN(0.0, 0.0, 0.0, 0.0)),
                        Line(VectorN(0.0, 0.0, 1.0, 0.0), VectorN(0.0, 0.0, 5.0, 0.0))
                )
