package com.ly.vehicleassistant.ui.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.domain.repository.VehicleRepository

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun VehicleProfileScreen(repository: VehicleRepository) {
    val viewModel = remember(repository) { VehicleProfileViewModel(repository) }
    val state by viewModel.uiState.collectAsState()
    var fuelMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("车辆档案")
        OutlinedTextField(
            value = state.brand,
            onValueChange = { viewModel.onEvent(VehicleProfileEvent.BrandChanged(it)) },
            label = { Text("品牌 *") },
            isError = state.brandError != null,
            supportingText = state.brandError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.model,
            onValueChange = { viewModel.onEvent(VehicleProfileEvent.ModelChanged(it)) },
            label = { Text("车型 *") },
            isError = state.modelError != null,
            supportingText = state.modelError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.licensePlate,
            onValueChange = { viewModel.onEvent(VehicleProfileEvent.LicensePlateChanged(it)) },
            label = { Text("车牌号（可选）") },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedTextField(
            value = state.mileage,
            onValueChange = { viewModel.onEvent(VehicleProfileEvent.MileageChanged(it)) },
            label = { Text("当前里程（公里）*") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = state.mileageError != null,
            supportingText = state.mileageError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth(),
        )
        ExposedDropdownMenuBox(
            expanded = fuelMenuExpanded,
            onExpandedChange = { fuelMenuExpanded = it },
        ) {
            OutlinedTextField(
                value = state.fuelType.displayName(),
                onValueChange = {},
                readOnly = true,
                label = { Text("能源类型") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = fuelMenuExpanded) },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(expanded = fuelMenuExpanded, onDismissRequest = { fuelMenuExpanded = false }) {
                FuelType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName()) },
                        onClick = {
                            viewModel.onEvent(VehicleProfileEvent.FuelTypeChanged(type))
                            fuelMenuExpanded = false
                        },
                    )
                }
            }
        }
        state.errorMessage?.let { Text(it) }
        Button(
            onClick = { viewModel.onEvent(VehicleProfileEvent.SaveClicked) },
            enabled = !state.isSaving,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.isSaving) "保存中…" else "保存车辆")
        }
        if (state.saved) Text("车辆档案已保存")
    }
}

private fun FuelType.displayName(): String = when (this) {
    FuelType.GASOLINE -> "汽油"
    FuelType.DIESEL -> "柴油"
    FuelType.ELECTRIC -> "纯电"
    FuelType.HYBRID -> "混动"
    FuelType.OTHER -> "其他"
}
