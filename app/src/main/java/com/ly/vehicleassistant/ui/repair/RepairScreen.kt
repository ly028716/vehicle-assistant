package com.ly.vehicleassistant.ui.repair

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewModelScope
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import kotlinx.coroutines.launch

@Composable
fun RepairScreen(repository: VehicleRepository) {
    val viewModel = remember(repository) { RepairViewModel(repository) }
    val state by viewModel.uiState.collectAsState()
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("维修记录") }
        item { OutlinedTextField(state.date, viewModel::updateDate, label = { Text("日期（yyyy-MM-dd）") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(state.mileage, viewModel::updateMileage, label = { Text("里程（公里）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(state.symptom, viewModel::updateSymptom, label = { Text("故障现象") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(state.repairItems, viewModel::updateRepairItems, label = { Text("维修项目") }, modifier = Modifier.fillMaxWidth()) }
        item { OutlinedTextField(state.costYuan, viewModel::updateCost, label = { Text("费用（元）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth()) }
        item { Button(onClick = { viewModel.viewModelScope.launch { viewModel.saveForm() } }, modifier = Modifier.fillMaxWidth()) { Text("保存维修记录") } }
        state.errorMessage?.let { message -> item { Text(message) } }
        if (state.saved) item { Text("维修记录已保存") }
        if (state.records.isEmpty()) {
            item { Text("暂无维修记录") }
        } else {
            items(state.records, key = { it.id }) { record ->
                Column {
                    Text("${record.date} · ${record.mileage} km · ${record.symptom}")
                    Text(record.repairItems)
                    Button(onClick = { viewModel.edit(record) }) { Text("编辑") }
                    Button(onClick = { pendingDeleteId = record.id }) { Text("删除") }
                }
            }
        }
    }
    pendingDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("删除维修记录") },
            text = { Text("删除后无法恢复，确定继续吗？") },
            confirmButton = { Button(onClick = { viewModel.viewModelScope.launch { viewModel.delete(id) }; pendingDeleteId = null }) { Text("删除") } },
            dismissButton = { Button(onClick = { pendingDeleteId = null }) { Text("取消") } },
        )
    }
}
