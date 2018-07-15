/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.graphics.gl

import java.nio.IntBuffer

/**
 * Buffer providing int memory for OpenGL out-params.
 */
val resultCode = IntBuffer.allocate(10)
        ?: throw RuntimeException("Cannot allocate result code")

/**
 * Return currently stored result code.
 */
fun resultCode() = resultCode[0]

/**
 * Execute [f], returning result code after-wards.
 */
inline fun resultCode(f: () -> Unit): Int {
    f()
    return resultCode()
}
