package io.jim.tesserapp.util

/**
 * Executes the given function [f] while holding the monitor of the function receiver.
 *
 * Equivalent to call `synchronized(this) { ... }`.
 */
inline fun <T : Any, R> T.synchronized(f: () -> R): R {
    return synchronized(this) {
        f()
    }
}
