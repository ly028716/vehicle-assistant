package com.ly.vehicleassistant.ui.maintenance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import com.ly.vehicleassistant.data.model.MaintenanceType

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MaintenanceScreen(repository: VehicleRepository) {
    val viewModel = remember(repository) { MaintenanceViewModel(repository) }
    val state by viewModel.uiState.collectAsState()
    var typeMenuExpanded by remember { mutableStateOf(false) }
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("保养记录") }
        item {
            OutlinedTextField(
                value = state.date,
                onValueChange = { viewModel.onEvent(MaintenanceEvent.DateChanged(it)) },
                label = { Text("保养日期（yyyy-MM-dd）*") },
                isError = state.dateError != null,
                supportingText = state.dateError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            ExposedDropdownMenuBox(
                expanded = typeMenuExpanded,
                onExpandedChange = { typeMenuExpanded = it },
            ) {
                OutlinedTextField(
                    value = state.type.displayName(),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("保养类型") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeMenuExpanded) },
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = typeMenuExpanded, onDismissRequest = { typeMenuExpanded = false }) {
                    MaintenanceType.entries.forEach { type ->
                        DropdownMenuItem(text = { Text(type.displayName()) }, onClick = {
                            viewModel.onEvent(MaintenanceEvent.TypeChanged(type))
                            typeMenuExpanded = false
                        })
                    }
                }
            }
        }
        item {
            OutlinedTextField(
                value = state.mileage,
                onValueChange = { viewModel.onEvent(MaintenanceEvent.MileageChanged(it)) },
                label = { Text("保养里程（公里）*") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = state.mileageError != null,
                supportingText = state.mileageError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = state.items,
                onValueChange = { viewModel.onEvent(MaintenanceEvent.ItemsChanged(it)) },
                label = { Text("保养项目 *") },
                isError = state.itemsError != null,
                supportingText = state.itemsError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            OutlinedTextField(
                value = state.costYuan,
                onValueChange = { viewModel.onEvent(MaintenanceEvent.CostChanged(it)) },
                label = { Text("费用（元，可选）") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            Button(
                onClick = { viewModel.onEvent(MaintenanceEvent.SaveClicked) },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("保存保养记录") }
        }
        state.errorMessage?.let { message -> item { Text(message) } }
        if (state.saved) item { Text("保养记录已保存") }
        if (state.records.isEmpty()) {
            item { Text("暂无保养记录") }
        } else {
            items(state.records, key = { it.id }) { record ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(record.items)
                    Text("${record.date} · ${record.mileage} km · ¥${record.costCents / 100.0}")
                    Button(onClick = { viewModel.edit(record) }) {
                        Text("编辑")
                    }
                    Button(onClick = { pendingDeleteId = record.id }) {
                        Text("删除")
                    }
                }
            }
        }
    }
    pendingDeleteId?.let { id ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text("删除保养记录") },
            text = { Text("删除后无法恢复，确定继续吗？") },
            confirmButton = {
                Button(onClick = {
                    viewModel.onEvent(MaintenanceEvent.DeleteClicked(id))
                    pendingDeleteId = null
                }) { Text("删除") }
            },
            dismissButton = { Button(onClick = { pendingDeleteId = null }) { Text("取消") } },
        )
    }
}

private fun MaintenanceType.displayName(): String = when (this) {
    MaintenanceType.ROUTINE -> "常规保养"
    MaintenanceType.MAJOR -> "大保养"
    MaintenanceType.ENGINE_OIL -> "机油"
    MaintenanceType.BRAKE_PAD -> "刹车片"
    MaintenanceType.TIRE -> "轮胎"
    MaintenanceType.BATTERY -> "电瓶"
    MaintenanceType.AIR_CONDITIONING -> "空调"
    MaintenanceType.OTHER -> "其他"
}
