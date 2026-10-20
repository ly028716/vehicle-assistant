package com.ly.vehicleassistant.ui.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ly.vehicleassistant.data.export.JsonVehicleDataExporter
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(repository: VehicleRepository, onVehicle: () -> Unit, onDeleted: () -> Unit) {
    val viewModel = remember(repository) { ProfileViewModel(repository, JsonVehicleDataExporter(repository)) }
    val state by viewModel.uiState.collectAsState()
    val vehicle by repository.observeVehicle().collectAsState(initial = null)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        val error = state.pendingExportJson?.let { json ->
            if (uri == null) null else runCatching {
                context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use { it.write(json) } ?: error("无法打开导出文件")
            }.exceptionOrNull()
        }
        viewModel.exportFinished(error)
    }
    LaunchedEffect(state.pendingExportJson) { state.pendingExportJson?.let { exportLauncher.launch("vehicle-assistant-${LocalDate.now()}.json") } }
    LaunchedEffect(state.deleted) { if (state.deleted) onDeleted() }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text("我的", style = MaterialTheme.typography.headlineMedium)
        Row(modifier = Modifier.fillMaxWidth().padding(top = 34.dp, bottom = 26.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("档", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                Text(vehicle?.let { "${it.brand} ${it.model}" } ?: "我的车辆", style = MaterialTheme.typography.titleLarge)
                Text("本地档案", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        SettingsLabel("VEHICLE")
        SettingsRow(Icons.Filled.DirectionsCar, "车辆档案", "车型、里程与能源类型", onVehicle)
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        SettingsLabel("DATA")
        SettingsRow(Icons.Filled.Download, "导出本地数据", "不包含车牌号") { scope.launch { viewModel.prepareExport() } }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        SettingsRow(Icons.Filled.Lock, "隐私说明", "所有数据仅保存在本机") { }
        Spacer(Modifier.weight(1f))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            "永久删除全部数据", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Medium,
            modifier = Modifier.fillMaxWidth().clickable { showDeleteConfirmation = true }.padding(vertical = 20.dp),
        )
        Text("Vehicle Assistant · v1.0", modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        state.errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
    }
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false }, title = { Text("永久删除全部数据") },
            text = { Text("车辆档案、保养和维修记录将永久删除，且无法恢复。") },
            confirmButton = { Button(onClick = { showDeleteConfirmation = false; scope.launch { viewModel.deleteAllData() } }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("永久删除") } },
            dismissButton = { Button(onClick = { showDeleteConfirmation = false }) { Text("取消") } },
        )
    }
}

@Composable
private fun SettingsLabel(label: String) = Text(label, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge, letterSpacing = 1.5.sp, modifier = Modifier.padding(top = 28.dp, bottom = 10.dp))

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
        Column(modifier = Modifier.padding(start = 18.dp).weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
