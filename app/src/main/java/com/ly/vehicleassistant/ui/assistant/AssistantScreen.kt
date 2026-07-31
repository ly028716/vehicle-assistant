package com.ly.vehicleassistant.ui.assistant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.data.assistant.FakeVehicleAssistantService
import com.ly.vehicleassistant.domain.repository.VehicleRepository

@Composable fun AssistantScreen(repository: VehicleRepository) {
    val vm = remember(repository) { AssistantViewModel(FakeVehicleAssistantService(repository)) }; val state by vm.uiState.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("本地用车助手"); LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) { items(state.messages) { Text(if (it.isUser) "你：${it.content}" else "助手：${it.content}") } }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) { listOf("我的车辆信息", "最近保养", "保养历史", "维修次数", "总费用").forEach { TextButton(onClick = { vm.send(it) }) { Text(it) } } }
        Row { OutlinedTextField(state.input, vm::updateInput, Modifier.weight(1f), label = { Text("输入问题") }); Button(onClick = { vm.send() }, enabled = !state.isLoading) { Text("发送") } }
        state.errorMessage?.let { Text(it) }; Text("仅基于本地记录提供信息，不构成故障诊断或维修建议。")
    }
}
