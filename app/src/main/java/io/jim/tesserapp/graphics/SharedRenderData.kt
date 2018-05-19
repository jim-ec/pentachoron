package io.jim.tesserapp.graphics

import io.jim.tesserapp.geometry.Geometry

/**
 * Data in that structure is owned by both, the render thread and ui thread (or any other).
 * Therefore, any access to it must be synchronized.
 */
data class SharedRenderData(

        /**
         * Geometry manager.
         */
        val drawDataProvider: DrawDataProvider

) {

    /**
     * Camera.
     */
    val camera = Camera()

    /**
     * The controlled geometry.
     */
    lateinit var controlledGeometry: Geometry

    /**
     * Executes [f], assuring thread synchronization on this render-data as long as [f] runs.
     */
    inline fun synchronized(f: (renderData: SharedRenderData) -> Unit) {
        synchronized(this) {
            f(this)
        }
    }

}
