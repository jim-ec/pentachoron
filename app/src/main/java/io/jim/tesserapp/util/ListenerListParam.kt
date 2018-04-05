package io.jim.tesserapp.util

/**
 * Provide a list for listeners.
 */
class ListenerListParam<T>(
        /**
         * Fired when a new listener is added.
         */
        val onAddedListener: (newListener: (T) -> Unit) -> Unit = {}
) {

    private val listeners = ArrayList<(T) -> Unit>()

    /**
     * Add [listener] to the list.
     */
    operator fun plusAssign(listener: (T) -> Unit) {
        listeners += listener
        onAddedListener(listener)
    }

    /**
     * Call all added listeners.
     */
    fun fire(param: T) {
        listeners.forEach { it(param) }
    }

}
