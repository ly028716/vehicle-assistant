package com.ly.vehicleassistant.ui.assistant

import com.ly.vehicleassistant.domain.assistant.VehicleAssistantService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModelTest {
    @Test fun sendAddsQuestionAndAnswer() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler)); try {
            val vm = AssistantViewModel(object : VehicleAssistantService { override suspend fun ask(question: String) = "本地回答" })
            vm.send("我的车"); advanceUntilIdle()
            assertEquals(2, vm.uiState.value.messages.size); assertEquals("本地回答", vm.uiState.value.messages.last().content)
        } finally { Dispatchers.resetMain() }
    }
    @Test fun blankInputIsIgnored() = runTest {
        Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler)); try {
            val vm = AssistantViewModel(object : VehicleAssistantService { override suspend fun ask(question: String) = "x" })
            vm.send("   "); advanceUntilIdle(); assertTrue(vm.uiState.value.messages.isEmpty())
        } finally { Dispatchers.resetMain() }
    }
}
