package com.ly.vehicleassistant.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.data.export.JsonVehicleDataExporter
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    repository: VehicleRepository,
    onVehicle: () -> Unit,
    onDeleted: () -> Unit,
) {
    val viewModel = remember(repository) {
        ProfileViewModel(repository, JsonVehicleDataExporter(repository))
    }
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json"),
    ) { uri ->
        val json = state.pendingExportJson
        val error = if (uri != null && json != null) {
            runCatching {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(json) }
                    ?: error("无法打开导出文件")
            }.exceptionOrNull()
        } else {
            null
        }
        viewModel.exportFinished(error)
    }

    LaunchedEffect(state.pendingExportJson) {
        if (state.pendingExportJson != null) {
            exportLauncher.launch("vehicle-assistant-${LocalDate.now()}.json")
        }
    }
    LaunchedEffect(state.deleted) {
        if (state.deleted) onDeleted()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("我的")
        Button(onClick = onVehicle, modifier = Modifier.fillMaxWidth()) {
            Text("车辆档案")
        }
        Button(
            onClick = { scope.launch { viewModel.prepareExport() } },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("导出车辆数据 JSON")
        }
        Button(
            onClick = { showDeleteConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("删除全部车辆数据")
        }
        state.errorMessage?.let { Text(it) }
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("删除全部车辆数据") },
            text = { Text("车辆档案、保养和维修记录将永久删除，且无法恢复。确定继续吗？") },
            confirmButton = {
                Button(onClick = {
                    showDeleteConfirmation = false
                    scope.launch { viewModel.deleteAllData() }
                }) { Text("确认删除") }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation = false }) { Text("取消") }
            },
        )
    }
}
