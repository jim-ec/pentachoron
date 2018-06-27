package io.jim.tesserapp.geometry

import io.jim.tesserapp.cpp.Transform
import io.jim.tesserapp.cpp.allocateNativeMemory

/**
 * A geometrical structure consisting of vertices.
 *
 * @property isFourDimensional
 * If true, a special geometry visualization is done in order to represent the four dimensional geometry
 * in a three dimensional space.
 */
class Geometry(
        val name: String,
        val onTransformUpdate: () -> Transform,
        val isFourDimensional: Boolean = false,
        val lines: List<Line>
) {
    
    /**
     * Represents this geometry in a string.
     */
    override fun toString() = name
    
    /**
     * Fast native buffer containing raw line data as doubles.
     * - line.size: Total line counts
     * - 2: Each line consists of two position vectors
     * - 4: Double components per position vector
     * - 8: Byte length of one double
     */
    val positions = allocateNativeMemory(lines.size * 2 * 4 * 8).asDoubleBuffer().apply {
        lines.forEach {
            it.points.forEach {
                it.components.forEach {
                    put(it)
                }
            }
        }
    }
    
}
