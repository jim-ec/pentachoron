package io.jim.tesserapp.util

import java.io.InputStream

/**
 * Read a stream, returning a single [String] including line break characters.
 */
fun readStream(stream: InputStream) =
        stream.bufferedReader().useLines { sequence: Sequence<String> ->
            sequence.reduce { a, b -> a + '\n' + b }
        }
