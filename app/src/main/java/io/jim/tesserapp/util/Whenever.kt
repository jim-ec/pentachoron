package io.jim.tesserapp.util

/**
 * Allows conditional branching.
 *
 * Call [f] when [condition] results to `true`.
 *
 * This' receiver is passed to [condition] and [f] as the parameter.
 *
 * This utility is intended to simplify some if constructs,
 * more specifically where a temporary variable must be created since if is used in both
 * the condition expression as well as in the conditional block.
 *
 * Instead, the temporary variable would be used as the receiver, to which all functions
 * have access via `it`.
 */
inline fun <T> T.whenever(
        condition: (T) -> Boolean,
        f: (T) -> Unit
) {
    if (condition(this)) {
        f(this)
    }
}
