package io.jim.tesserapp.graphics

/**
 * Data in that structure is owned by both, the render thread and ui thread (or any other).
 * Therefore, any access to it must be synchronized.
 *
 * @property camera Camera related preferences.
 */
data class SharedRenderData(
        val camera: Camera = Camera()
)
