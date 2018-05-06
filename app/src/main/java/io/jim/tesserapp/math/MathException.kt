package io.jim.tesserapp.math

/**
 * An error indicating a math-related error like zero-division due to invalid parameters.
 * @param msg Error message.
 */
class MathException(msg: String) :
        RuntimeException("Math error: $msg")
