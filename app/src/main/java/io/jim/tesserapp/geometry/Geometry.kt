/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.geometry

import io.jim.tesserapp.graphics.gl.Color
import io.jim.tesserapp.util.allocateNativeMemory

/**
 * A geometrical structure consisting of vertices.
 *
 * @property isFourDimensional
 * If true, a special geometry visualization is done in order to represent the four dimensional geometry
 * in a three dimensional space.
 */
class Geometry(
        val name: String,
        val onTransformUpdate: () -> Transform = { Transform() },
        val isFourDimensional: Boolean = false,
        lines: List<Line>,
        val color: Color
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
    val positions = allocateNativeMemory(lines.size * 2 * 4 * 8).asDoubleBuffer()!!.apply {
        lines.forEach {
            it.points.forEach {
                put(it.x)
                put(it.y)
                put(it.z)
                put(it.q)
            }
        }
        rewind()
    }.asReadOnlyBuffer()!!
    
}
