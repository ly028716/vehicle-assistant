package com.ly.vehicleassistant.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.R
import com.ly.vehicleassistant.data.model.ReminderStatus
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.domain.repository.VehicleRepository

@Composable
fun HomeScreen(
    repository: VehicleRepository,
    onEditVehicle: () -> Unit,
    onAddMaintenance: () -> Unit,
    onAddRepair: () -> Unit,
) {
    val viewModel = remember(repository) { HomeViewModel(repository) }
    val summary by viewModel.uiState.collectAsState()

    LazyColumn(
        contentPadding = PaddingValues(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item { HomeHeader() }
        val vehicle = summary.vehicle
        if (vehicle == null) {
            item { EmptyGarage(onEditVehicle) }
        } else {
            item { VehicleHero(vehicle) }
            item { MaintenancePriority(summary.reminderStatus, onAddMaintenance) }
            item {
                LatestService(
                    summary = summary,
                    onAddMaintenance = onAddMaintenance,
                    onAddRepair = onAddRepair,
                )
            }
            item { SpendingSummary(summary) }
        }
    }
}

@Composable
private fun HomeHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("车辆概览", style = MaterialTheme.typography.headlineMedium)
        Text(
            "把每一次养护，变成安心出行的底气",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun EmptyGarage(onEditVehicle: () -> Unit) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.DirectionsCar,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp).size(26.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Text("你的车库还没有车辆", style = MaterialTheme.typography.titleLarge)
            Text(
                "先建立车辆档案，保养提醒、费用与记录才会为你服务。",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
            Button(onClick = onEditVehicle, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("添加车辆档案")
            }
        }
    }
}

@Composable
private fun VehicleHero(vehicle: Vehicle) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column {
            Image(
                painter = painterResource(R.drawable.vehicle_home_hero),
                contentDescription = "车辆展示图",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp)
                    .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
            )
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.DirectionsCar,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("当前车辆", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${vehicle.brand} ${vehicle.model}", style = MaterialTheme.typography.titleLarge)
                Text(
                    "当前里程  ${vehicle.currentMileage} km",
                    style = MaterialTheme.typography.displaySmall,
                )
            }
        }
    }
}

@Composable
private fun MaintenancePriority(status: ReminderStatus, onAddMaintenance: () -> Unit) {
    val isAttentionNeeded = status == ReminderStatus.DUE_SOON || status == ReminderStatus.OVERDUE
    val contentColor = if (isAttentionNeeded) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(14.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Build,
                        contentDescription = null,
                        modifier = Modifier.padding(10.dp).size(24.dp),
                        tint = contentColor,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("下一次保养", style = MaterialTheme.typography.labelLarge, color = contentColor)
                    Text(status.displayName(), style = MaterialTheme.typography.titleLarge)
                }
            }
            Text(status.description(), color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(
                onClick = onAddMaintenance,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("添加保养记录")
            }
        }
    }
}

@Composable
private fun LatestService(
    summary: HomeSummary,
    onAddMaintenance: () -> Unit,
    onAddRepair: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("最近服务", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onAddMaintenance) { Text("查看保养") }
        }
        Surface(
            modifier = Modifier.border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(22.dp)),
            shape = RoundedCornerShape(22.dp),
        ) {
            Column {
                ServiceRow(
                    icon = Icons.Filled.History,
                    title = summary.latestMaintenance?.items ?: "还没有保养记录",
                    detail = summary.latestMaintenance?.let { "${it.date} · ${it.mileage} km" } ?: "记录一次保养，建立你的养护节奏",
                    value = summary.latestMaintenance?.let { "¥${it.costCents / 100.0}" } ?: "去添加",
                    onClick = onAddMaintenance,
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                ServiceRow(
                    icon = Icons.Filled.Build,
                    title = summary.latestRepair?.repairItems ?: "还没有维修记录",
                    detail = summary.latestRepair?.let { "${it.date} · ${it.mileage} km" } ?: "维修事项会在这里沉淀",
                    value = summary.latestRepair?.let { "¥${it.costCents / 100.0}" } ?: "去添加",
                    onClick = onAddRepair,
                )
            }
        }
    }
}

@Composable
private fun ServiceRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    detail: String,
    value: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(14.dp),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.padding(10.dp).size(20.dp),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
        }
        TextButton(onClick = onClick) {
            Text(value, fontWeight = FontWeight.SemiBold)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
private fun SpendingSummary(summary: HomeSummary) {
    val total = (summary.maintenanceCostCents + summary.repairCostCents) / 100.0
    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(22.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(14.dp)) {
                Icon(
                    imageVector = Icons.Filled.Payments,
                    contentDescription = null,
                    modifier = Modifier.padding(10.dp).size(22.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text("累计养车支出", style = MaterialTheme.typography.labelLarge)
                Text("保养与维修的历史花费", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            }
            Text("¥$total", style = MaterialTheme.typography.titleLarge)
        }
    }
}

private fun ReminderStatus.displayName(): String = when (this) {
    ReminderStatus.NO_RECORD -> "尚未建立保养节奏"
    ReminderStatus.NOT_DUE -> "保养状态良好"
    ReminderStatus.DUE_SOON -> "保养即将到期"
    ReminderStatus.OVERDUE -> "建议尽快保养"
}

private fun ReminderStatus.description(): String = when (this) {
    ReminderStatus.NO_RECORD -> "添加第一条保养记录后，会按里程和日期给你提醒。"
    ReminderStatus.NOT_DUE -> "当前保养周期正常，继续保持良好的记录习惯。"
    ReminderStatus.DUE_SOON -> "你的车辆已接近保养周期，建议提前安排时间。"
    ReminderStatus.OVERDUE -> "你的车辆已超过建议保养周期，建议尽快完成检查。"
}
