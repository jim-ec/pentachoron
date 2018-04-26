package io.jim.tesserapp.graphics

import io.jim.tesserapp.math.Vector
import io.jim.tesserapp.util.BYTE_LENGTH
import io.jim.tesserapp.util.Buffer

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

) : Buffer.Element {

    override val floats = listOf(
            position.x, position.y, position.z,
            color.red, color.green, color.blue,
            modelIndex.toFloat()
    )

    companion object {
        internal const val COMPONENTS_PER_POSITION = 3
        internal const val COMPONENTS_PER_COLOR = 3
        internal const val COMPONENTS_PER_MODEL_INDEX = 1

        internal const val COMPONENTS_PER_VERTEX =
                COMPONENTS_PER_POSITION + COMPONENTS_PER_COLOR + COMPONENTS_PER_MODEL_INDEX

        internal val STRIDE_BYTES = COMPONENTS_PER_VERTEX * Float.BYTE_LENGTH

        internal val OFFSET_POSITION_BYTES = 0 * Float.BYTE_LENGTH

        internal val OFFSET_COLOR_BYTES = COMPONENTS_PER_POSITION * Float.BYTE_LENGTH

        internal val OFFSET_MODEL_INDEX_BYTES =
                (COMPONENTS_PER_POSITION + COMPONENTS_PER_COLOR) * Float.BYTE_LENGTH
    }

}
