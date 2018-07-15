/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.util

import java.io.InputStream

/**
 * Read a stream, returning a single [String] including line break characters.
 */
fun readStream(stream: InputStream) =
        stream.bufferedReader().useLines { sequence: Sequence<String> ->
            sequence.reduce { a, b -> a + '\n' + b }
        }
