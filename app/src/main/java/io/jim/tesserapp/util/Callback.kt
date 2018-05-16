package io.jim.tesserapp.util


/**
 * Wraps a single nullable function.
 */
class Callback {

    private var callback: (() -> Unit)? = null

    /**
     * When this callback is invoked, it calls [c].
     * Replaces the old callback function.
     */
    fun set(c: () -> Unit) {
        callback = c
    }

    /**
     * Unset the current callback to `null`.
     */
    fun unset() {
        callback = null
    }

    /**
     * Invokes the callback function.
     * Does nothing if that callback function is `null`.
     */
    operator fun invoke() {
        callback?.invoke()
    }

}
