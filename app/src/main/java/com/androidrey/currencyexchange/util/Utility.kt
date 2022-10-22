package com.androidrey.currencyexchange.util

import java.math.RoundingMode
import java.text.DecimalFormat

fun roundOffDecimal(number: Double): Double? {
    val df = DecimalFormat("#.####")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(number).toDouble()
}