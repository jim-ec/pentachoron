package io.jim.tesserapp.graphics

/**
 * Byte length of a single float.
 */
const val FLOAT_BYTE_LENGTH = 4

/**
 * Count of floats each vertex attribute consists of.
 * Due to alignment, each attribute has 4 float values, regardless of how many
 * it actually uses.
 */
const val ATTRIBUTE_FLOATS = 4

/**
 * Counts of different attributes.
 * - Position
 * - Color
 */
const val ATTRIBUTE_COUNTS = 2

/**
 * Count of floats each vertex consists of.
 */
const val VERTEX_FLOATS = ATTRIBUTE_COUNTS * ATTRIBUTE_FLOATS

/**
 * Count of bytes each vertex consists of.
 */
const val VERTEX_STRIDE = VERTEX_FLOATS * FLOAT_BYTE_LENGTH

/**
 * Position attribute offset, in bytes.
 */
const val VERTEX_OFFSET_POSITION = 0

/**
 * Color attribute offset, in bytes.
 */
const val VERTEX_OFFSET_COLOR = VERTEX_OFFSET_POSITION + ATTRIBUTE_FLOATS * FLOAT_BYTE_LENGTH
