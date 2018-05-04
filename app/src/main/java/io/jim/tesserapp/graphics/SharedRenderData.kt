package io.jim.tesserapp.graphics

/**
 * Data in that structure is owned by both, the render thread and ui thread (or any other).
 * Therefore, any access to it must be synchronized.
 */
data class SharedRenderData(

        /**
         * Geometry manager.
         */
        val geometryManager: GeometryManager

) {

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
