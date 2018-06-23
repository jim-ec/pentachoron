package io.jim.tesserapp.geometry

import io.jim.tesserapp.math.vector.VectorN

data class Line(
        val start: VectorN,
        val end: VectorN,
        var color: SymbolicColor = SymbolicColor.PRIMARY
) {
    
    /**
     * Calls [f] for both the start position and the end position.
     */
    inline fun forEachPosition(f: (position: VectorN) -> Unit) {
        f(start)
        f(end)
    }
    
}
