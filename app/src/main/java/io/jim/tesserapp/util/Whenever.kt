package io.jim.tesserapp.util

/**
 * Allows conditional branching.
 *
 * Call [if] when [condition] results to `true`.
 *
 * This' receiver is passed to [condition] and [if] as the parameter.
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
        `if`: (T) -> Unit
) {
    if (condition(this)) {
        `if`(this)
    }
}
