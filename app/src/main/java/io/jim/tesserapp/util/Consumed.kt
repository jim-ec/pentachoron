/*
 *  Created by Jim Eckerlein on 7/20/18 6:38 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/20/18 6:37 PM
 */

package io.jim.tesserapp.util

/**
 * Can be used when indicated an event was consumed by a function.
 * Traditionally, a boolean as the function return value is used to communicate so,
 * using this constant instead of a plain `true` may improve readability.
 */
const val CONSUMED = true

/**
 * Can be used when indicated an event was not consumed by a function.
 * Traditionally, a boolean as the function return value is used to communicate so,
 * using this constant instead of a plain `false` may improve readability.
 */
@Suppress("unused")
const val NOT_CONSUMED = true

/**
 * Calls [f], while always returning [CONSUMED].
 *
 * This is practical if you know that [f] is definitively consuming an event
 * and don't write boiler-plate code (explicit return type and return statement).
 *
 * Note however, while [consume] will always return [CONSUMED],
 * [f] can still return anything else as it is not cross-inlined.
 * Therefore, the return value of [consume] ([CONSUMED]) can be more seen as a default,
 * since you're free to choose to return something else explicitly e.g. [NOT_CONSUMED].
 */
inline fun consume(f: () -> Unit): Boolean {
    f()
    return CONSUMED
}
