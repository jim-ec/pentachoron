package io.jim.tesserapp.util

/**
 * Provide a list for listeners.
 */
class ListenerListParam<T> {

    private val listeners = HashSet<(T) -> Unit>()

    /**
     * Add [listener] to the list.
     */
    operator fun plusAssign(listener: (T) -> Unit) {
        listeners += listener
    }

    /**
     * Call all added listeners.
     */
    fun fire(parameter: T) {
        listeners.forEach { it(parameter) }
    }

    operator fun minusAssign(listener: (T) -> Unit) {
        listeners -= listener
    }

}
