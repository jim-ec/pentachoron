/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.util

/**
 * Executes the given function [f] while holding the monitor of the function receiver.
 *
 * Equivalent to call `synchronized(this) { ... }`.
 */
inline fun <T : Any, R> T.synchronized(crossinline f: T.() -> R): R {
    return synchronized(this) {
        f()
    }
}
