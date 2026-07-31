package com.ly.vehicleassistant.ui.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.vehicleassistant.domain.assistant.VehicleAssistantService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AssistantMessage(val content: String, val isUser: Boolean)
data class AssistantUiState(val messages: List<AssistantMessage> = emptyList(), val input: String = "", val isLoading: Boolean = false, val errorMessage: String? = null)

class AssistantViewModel(private val service: VehicleAssistantService) : ViewModel() {
    private val mutableUiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = mutableUiState.asStateFlow()
    fun updateInput(value: String) = mutableUiState.update { it.copy(input = value) }
    fun send(question: String = mutableUiState.value.input) {
        if (question.isBlank() || mutableUiState.value.isLoading) return
        mutableUiState.update { it.copy(messages = it.messages + AssistantMessage(question, true), input = "", isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            runCatching { service.ask(question) }.onSuccess { answer ->
                mutableUiState.update { it.copy(messages = it.messages + AssistantMessage(answer, false), isLoading = false) }
            }.onFailure { error -> mutableUiState.update { it.copy(isLoading = false, errorMessage = error.message ?: "回答失败") } }
        }
    }
}
