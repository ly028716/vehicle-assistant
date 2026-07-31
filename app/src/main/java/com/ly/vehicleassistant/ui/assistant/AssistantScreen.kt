package com.ly.vehicleassistant.ui.assistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.data.assistant.FakeVehicleAssistantService
import com.ly.vehicleassistant.domain.repository.VehicleRepository

@Composable
fun AssistantScreen(repository: VehicleRepository) {
    val viewModel = remember(repository) { AssistantViewModel(FakeVehicleAssistantService(repository)) }
    val state by viewModel.uiState.collectAsState()
    val prompts = listOf("我的车辆信息", "最近保养", "保养历史", "维修次数", "总费用")

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("本地用车助手", style = MaterialTheme.typography.headlineMedium)
        Text("只读取你的本地记录，不联网、不诊断故障。", color = MaterialTheme.colorScheme.onSurfaceVariant)
        if (state.messages.isEmpty()) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(22.dp),
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("今天想了解什么？", style = MaterialTheme.typography.titleLarge)
                    Text("从车辆、保养、维修和花费中选一个问题开始。")
                }
            }
        }
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(state.messages) { _, message ->
                MessageBubble(content = message.content, isUser = message.isUser)
            }
        }
        LazyRow(
            modifier = Modifier.height(40.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(prompts) { prompt ->
                FilterChip(
                    selected = false,
                    onClick = { viewModel.send(prompt) },
                    label = { Text(prompt) },
                )
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = state.input,
                onValueChange = viewModel::updateInput,
                modifier = Modifier.weight(1f),
                label = { Text("输入问题") },
                singleLine = true,
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { viewModel.send() }, enabled = !state.isLoading) {
                androidx.compose.material3.Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "发送")
            }
        }
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
}

@Composable
private fun MessageBubble(content: String, isUser: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start) {
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                contentColor = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
            ),
        ) {
            Text(content, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
        }
    }
}
