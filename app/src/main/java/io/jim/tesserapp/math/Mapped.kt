package io.jim.tesserapp.math

/**
 * Maps a value within an input range to its corresponding value inside an output range.
 * @param x Source value of input range.
 * @param input Input range.
 * @param output Output range.
 */
fun mapped(x: Double, input: ClosedRange<Double>, output: ClosedRange<Double>) =
        (x - input.start) / (input.endInclusive - input.start) *
                (output.endInclusive - output.start) + output.start
