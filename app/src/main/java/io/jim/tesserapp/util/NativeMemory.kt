/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.util

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Return a directly (natively) allocated byte buffer with native byte order.
 * @param bytes Size of memory to be allocated, in bytes.
 */
fun allocateNativeMemory(bytes: Int) =
        ByteBuffer.allocateDirect(bytes)!!.apply {
            order(ByteOrder.nativeOrder())
        }

