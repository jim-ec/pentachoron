package io.jim.tesserapp.math

import java.math.RoundingMode
import java.text.DecimalFormat

object Format {

    val decimalFormat = DecimalFormat(" 0.00;-0.00").apply {
        roundingMode = RoundingMode.HALF_UP
    }

}
