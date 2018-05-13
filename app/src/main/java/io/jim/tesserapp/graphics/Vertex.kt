package io.jim.tesserapp.graphics

import io.jim.tesserapp.math.vector.Vector3d

/**
 * One individual vertex.
 */
data class Vertex(

        /**
         * Position of vertex.
         */
        val position: Vector3d,

        /**
         * Color of vertex.
         */
        val color: Color,

        /**
         * Index of model matrix for this vertex.
         */
        val modelIndex: Int

) {

    companion object {
        internal const val COMPONENTS_PER_POSITION = 3
        internal const val COMPONENTS_PER_COLOR = 3
        internal const val COMPONENTS_PER_MODEL_INDEX = 1
    }

}
