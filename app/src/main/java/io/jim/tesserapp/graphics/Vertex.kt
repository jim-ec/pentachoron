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
        val color: Color,

        /**
         * Index of model matrix for this vertex.
         */
        private val modelIndex: Int

) : Iterable<Float> {

    private val floats = listOf(
            position.x, position.y, position.z,
            color.red, color.green, color.blue,
            modelIndex.toFloat()
    )

    override operator fun iterator() = floats.iterator()

}
