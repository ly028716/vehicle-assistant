package com.ly.vehicleassistant.ui.home

import java.text.NumberFormat
import java.util.Locale

fun formatYuan(costCents: Long): String {
    val integerYuan = costCents / 100
    val cents = kotlin.math.abs(costCents % 100)
    if (cents == 0L) {
        return "¥${NumberFormat.getIntegerInstance(Locale.CHINA).format(integerYuan)}"
    }

    return String.format(Locale.CHINA, "¥%,.2f", costCents / 100.0)
}
