package com.ly.vehicleassistant.ui.common

import org.junit.Assert.assertEquals
import org.junit.Test

class UiStateTest {
    @Test
    fun contentStateKeepsItsValue() {
        val state: UiState<String> = UiState.Content("vehicle")

        assertEquals("vehicle", (state as UiState.Content).value)
    }

    @Test
    fun emptyStateKeepsItsMessage() {
        val state = UiState.Empty("No vehicle")

        assertEquals("No vehicle", state.message)
    }
}
