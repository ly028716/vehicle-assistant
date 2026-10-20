package com.ly.vehicleassistant.ui.assistant

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.data.assistant.FakeVehicleAssistantService
import com.ly.vehicleassistant.domain.repository.VehicleRepository

@Composable
fun AssistantScreen(repository: VehicleRepository) {
    val viewModel = remember(repository) { AssistantViewModel(FakeVehicleAssistantService(repository)) }
    val state by viewModel.uiState.collectAsState()
    val prompts = listOf("上次保养是什么时候？", "今年花了多少钱？", "为什么提示该保养？")
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("本地用车助手", style = MaterialTheme.typography.headlineMedium)
        Row(modifier = Modifier.padding(top = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Lock, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text("仅基于本地车辆记录 · 不联网", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodyMedium)
        }
        LazyColumn(modifier = Modifier.weight(1f).padding(top = 44.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
            if (state.messages.isEmpty()) {
                item { Text("从你的用车记录开始", style = MaterialTheme.typography.titleLarge) }
                item { Text("我只查询与整理保存在本机的车辆、保养和维修记录，不提供故障诊断。", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp, bottom = 28.dp)) }
                items(prompts.size) { index -> PromptRow(prompts[index]) { viewModel.send(prompts[index]) } }
            } else {
                itemsIndexed(state.messages) { _, message -> AssistantEntry(message) }
            }
            if (state.isLoading) item { LoadingEntry() }
        }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 8.dp)) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(value = state.input, onValueChange = viewModel::updateInput, modifier = Modifier.weight(1f), placeholder = { Text("问一个关于车辆记录的问题") }, singleLine = true, enabled = !state.isLoading)
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = { viewModel.send() }, enabled = state.input.isNotBlank() && !state.isLoading, modifier = Modifier.size(52.dp)) { Icon(Icons.AutoMirrored.Filled.Send, "发送问题", tint = MaterialTheme.colorScheme.onSurface) }
        }
    }
}

@Composable
private fun PromptRow(prompt: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(prompt, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
private fun AssistantEntry(message: AssistantMessage) {
    Column(modifier = Modifier.padding(bottom = 22.dp)) {
        Text(if (message.isUser) "你的问题" else "本地助手", color = if (message.isUser) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        Text(message.content, style = if (message.isUser) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 6.dp))
        if (!message.isUser) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.padding(top = 18.dp))
    }
}

@Composable
private fun LoadingEntry() {
    Row(modifier = Modifier.padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
        Spacer(Modifier.width(10.dp))
        Text("正在整理本地记录…", color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
