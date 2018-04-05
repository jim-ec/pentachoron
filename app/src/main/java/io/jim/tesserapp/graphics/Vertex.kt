package io.jim.tesserapp.graphics

import io.jim.tesserapp.math.Vector

/**
 * One individual vertex.
 */
data class Vertex(

        /**
         * Position of vertex.
         */
        val position: Vector,

        /**
         * Color of vertex.
         */
        val color: Color

)
