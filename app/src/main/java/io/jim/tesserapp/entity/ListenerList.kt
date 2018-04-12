package io.jim.tesserapp.entity

/**
 * Provide a list for listeners.
 */
class ListenerList {

    private val listeners = ArrayList<() -> Unit>()

    /**
     * Add [listener] to the list.
     */
    operator fun plusAssign(listener: () -> Unit) {
        listeners += listener
    }

    /**
     * Call all added listeners.
     */
    fun fire() {
        listeners.forEach { it() }
    }

}
