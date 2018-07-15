/*
 *  Created by Jim Eckerlein on 7/15/18 4:04 PM
 *  Copyright (c) 2018 . All rights reserved.
 *  Last modified 7/15/18 4:03 PM
 */

package io.jim.tesserapp.math

import java.math.RoundingMode
import java.text.DecimalFormat

/**
 * Formats [number] into a string, focusing on readability.
 * If [number] is negative zero, no unary minus is printed.
 */
fun formatNumber(number: Double) =
        decimalFormat.format(if ((number * 100).toInt() == 0) 0.0 else number)
                ?: throw RuntimeException("Formatted decimal string is null")

private val decimalFormat = DecimalFormat(" 0.00;-0.00").apply {
    roundingMode = RoundingMode.HALF_UP
}
