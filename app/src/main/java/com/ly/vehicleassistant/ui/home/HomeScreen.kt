package com.ly.vehicleassistant.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
    onViewRecords: () -> Unit,
) {
    val viewModel = remember(repository) { HomeViewModel(repository) }
    val summary by viewModel.uiState.collectAsState()
    LazyColumn(
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { HomeHeader() }
        summary.vehicle?.let { vehicle ->
            item { VehicleHero(vehicle, onEditVehicle) }
            item { MaintenanceSignal(summary.reminderStatus, onAddMaintenance) }
            item { QuietSummary(summary) }
            item { RecentRecord(summary, onViewRecords, onAddMaintenance, onAddRepair) }
        } ?: item { EmptyGarage(onEditVehicle) }
    }
}

@Composable
private fun HomeHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("我的爱车", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.weight(1f))
        Icon(Icons.Filled.NotificationsNone, contentDescription = "提醒", modifier = Modifier.size(25.dp))
    }
}

@Composable
private fun VehicleHero(vehicle: Vehicle, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(360.dp).clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(R.drawable.vehicle_home_hero_premium), contentDescription = "${vehicle.brand} ${vehicle.model} 车辆展示图",
            contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize(),
        )
        Box(
            Modifier.matchParentSize().background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    .58f to Color.Transparent,
                    1f to Color(0x990D1414),
                ),
            ),
        )
        Column(
            modifier = Modifier.align(Alignment.TopStart).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text("本地车辆档案", color = Color.White.copy(alpha = .72f), style = MaterialTheme.typography.labelLarge)
            Text("${vehicle.brand} ${vehicle.model}", color = Color.White, style = MaterialTheme.typography.titleLarge)
        }
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text("${vehicle.currentMileage.formatMileage()} km", color = Color.White, style = MaterialTheme.typography.displaySmall)
            Text("总里程", color = Color.White.copy(alpha = .76f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun MaintenanceSignal(status: ReminderStatus, onClick: () -> Unit) {
    val progress = when (status) {
        ReminderStatus.NO_RECORD -> .06f
        ReminderStatus.NOT_DUE -> .5f
        ReminderStatus.DUE_SOON -> .82f
        ReminderStatus.OVERDUE -> 1f
    }
    val tone = if (status == ReminderStatus.DUE_SOON || status == ReminderStatus.OVERDUE) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    Column(
        modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("MAINTENANCE", color = tone, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
        Text(status.heroText(), style = MaterialTheme.typography.titleLarge)
        Box(Modifier.fillMaxWidth().height(3.dp).clip(RoundedCornerShape(4.dp)).background(MaterialTheme.colorScheme.outlineVariant)) {
            Box(Modifier.fillMaxWidth(progress).height(3.dp).background(tone))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(status.description(), modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            Text("查看建议", color = tone, fontWeight = FontWeight.SemiBold, modifier = Modifier.clickable(onClick = onClick).padding(6.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = tone, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun QuietSummary(summary: HomeSummary) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        SummaryLine("已记录", "${summary.recordCount} 次")
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        SummaryLine("累计投入", formatYuan(summary.totalCostCents))
    }
}

@Composable
private fun SummaryLine(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 17.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.weight(1f))
        Text(value, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun RecentRecord(summary: HomeSummary, onViewRecords: () -> Unit, onAddMaintenance: () -> Unit, onAddRepair: () -> Unit) {
    val maintenance = summary.latestMaintenance
    val repair = summary.latestRepair
    Column(modifier = Modifier.padding(horizontal = 24.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("最近记录", style = MaterialTheme.typography.titleLarge)
        when {
            maintenance != null -> RecordRow("保养", maintenance.items, "${maintenance.date} · ${maintenance.mileage.formatMileage()} km", formatYuan(maintenance.costCents), onViewRecords)
            repair != null -> RecordRow("维修", repair.repairItems, "${repair.date} · ${repair.mileage.formatMileage()} km", formatYuan(repair.costCents), onViewRecords)
            else -> EmptyRecords(onAddMaintenance, onAddRepair)
        }
    }
}

@Composable
private fun RecordRow(kind: String, title: String, detail: String, cost: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(kind, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(detail, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(cost, style = MaterialTheme.typography.titleMedium)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun EmptyRecords(onAddMaintenance: () -> Unit, onAddRepair: () -> Unit) {
    Surface(border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("还没有用车记录", style = MaterialTheme.typography.titleMedium)
            Text("从一次保养或维修开始，留下可追溯的用车档案。", color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("新增保养", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable(onClick = onAddMaintenance).padding(4.dp))
                Text("新增维修", color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable(onClick = onAddRepair).padding(4.dp))
            }
        }
    }
}

@Composable
private fun EmptyGarage(onEditVehicle: () -> Unit) {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Icon(Icons.Filled.DirectionsCar, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(36.dp))
        Text("从一辆车开始", style = MaterialTheme.typography.headlineMedium)
        Text("建立车辆档案后，保养、维修、费用与提醒都会汇集在这里。", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Button(onClick = onEditVehicle) { Icon(Icons.Filled.Add, null); Spacer(Modifier.width(6.dp)); Text("添加车辆档案") }
    }
}

private fun Int.formatMileage(): String = java.lang.String.format(java.util.Locale.CHINA, "%,d", this)

private fun ReminderStatus.heroText(): String = when (this) {
    ReminderStatus.NO_RECORD -> "建立第一条保养记录"
    ReminderStatus.NOT_DUE -> "当前保养节奏良好"
    ReminderStatus.DUE_SOON -> "已接近下一次保养"
    ReminderStatus.OVERDUE -> "建议尽快安排保养"
}

private fun ReminderStatus.description(): String = when (this) {
    ReminderStatus.NO_RECORD -> "记录日期与里程后，将按实际情况提示你。"
    ReminderStatus.NOT_DUE -> "继续保持当前的保养节奏。"
    ReminderStatus.DUE_SOON -> "建议提前安排时间，避免错过周期。"
    ReminderStatus.OVERDUE -> "已超过建议周期，请尽快完成检查。"
}
