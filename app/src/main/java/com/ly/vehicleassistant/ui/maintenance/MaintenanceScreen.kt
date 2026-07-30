package com.ly.vehicleassistant.ui.maintenance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.domain.repository.VehicleRepository

@Composable
fun MaintenanceScreen(repository: VehicleRepository) {
    val viewModel = remember(repository) { MaintenanceViewModel(repository) }
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item { Text("保养记录") }
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
                }
            }
        }
    }
}
