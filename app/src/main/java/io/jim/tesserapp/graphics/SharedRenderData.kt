package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry

/**
 * Data in that structure is owned by both, the render thread and ui thread (or any other).
 * Therefore, any access to it must be synchronized.
 *
 * @property drawDataProvider
 * Provider for data necessary to draw anything.
 *
 * @property featuredGeometry
 * Featured geometry.
 * Is automatically added to the [drawDataProvider].
 */
data class SharedRenderData(

        /**
         * Geometry manager.
         */
        val drawDataProvider: DrawDataProvider,

        /**
         * The controlled geometry.
         */
        val featuredGeometry: Geometry

) {

    init {
        drawDataProvider += featuredGeometry
    }

    /**
     * Camera.
     */
    val camera = Camera()

    /**
     * Executes [f], assuring thread synchronization on this render-data as long as [f] runs.
     */
    inline fun synchronized(f: (renderData: SharedRenderData) -> Unit) {
        synchronized(this) {
            f(this)
        }
    }

}
