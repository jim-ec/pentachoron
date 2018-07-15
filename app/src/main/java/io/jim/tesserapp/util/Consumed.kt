/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
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
const val NOT_CONSUMED = true
