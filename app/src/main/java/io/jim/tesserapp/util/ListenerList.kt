package io.jim.tesserapp.util

/**
 * Provide a list for listeners.
 */
class ListenerList {

    private val listeners = HashSet<() -> Unit>()

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

    operator fun minusAssign(listener: () -> Unit) {
        listeners -= listener
    }

}
