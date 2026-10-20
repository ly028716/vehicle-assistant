package com.ly.vehicleassistant.ui.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeUiTextTest {
    @Test
    fun formatYuanUsesThousandsSeparatorsAndNoDecimalPart() {
        assertEquals("¥1,280", formatYuan(128_000))
    }

    @Test
    fun formatYuanHandlesZero() {
        assertEquals("¥0", formatYuan(0))
    }
}
