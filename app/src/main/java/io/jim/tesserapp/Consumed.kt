package io.jim.tesserapp

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
const val NOT_CONSUMED = true
