package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

typealias FourthDimensionVisualizer = (VectorN) -> VectorN

val projectWireframe: FourthDimensionVisualizer = { pos: VectorN -> pos / pos.q }

val collapseZ: FourthDimensionVisualizer = { pos: VectorN -> with(pos) { VectorN(x, y, q, 0.0) } }
