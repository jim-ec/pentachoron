package io.jim.tesserapp.util

import android.support.annotation.CheckResult

/**
 * Return a function, which whenever called, calls [callback] with the difference value
 * between the *old* one and *new* one.
 *
 * - The **new value** is received when calling the returned function as a parameter.
 *
 * - The **old value** is either the *new value* from previous invocation or [initialValue]
 * if the returned function has not been called yet.
 */
@CheckResult
fun mapDifference(
        initialValue: Double,
        callback: (difference: Double) -> Unit
): (Double) -> Unit = run {
    var old = initialValue
    { newValue: Double ->
        callback(newValue - old)
        old = newValue
    }
}
