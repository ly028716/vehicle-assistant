package com.ly.vehicleassistant.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ly.vehicleassistant.data.model.ReminderStatus
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("用车概览")
        val vehicle = summary.vehicle
        if (vehicle == null) {
            Text("还没有车辆档案，先添加一辆车开始记录用车信息。")
            Button(onClick = onEditVehicle, modifier = Modifier.fillMaxWidth()) {
                Text("添加车辆档案")
            }
            return@Column
        }

        SummaryCard("当前车辆") {
            Text("${vehicle.brand} ${vehicle.model}")
            Text("当前里程：${vehicle.currentMileage} km")
        }
        SummaryCard("保养提醒") {
            Text(summary.reminderStatus.displayName())
            Text(summary.reminderStatus.description())
        }
        SummaryCard("最近记录") {
            Text(summary.latestMaintenance?.let { "最近保养：${it.date} · ${it.items}" } ?: "暂无保养记录")
            Text(summary.latestRepair?.let { "最近维修：${it.date} · ${it.repairItems}" } ?: "暂无维修记录")
        }
        SummaryCard("费用统计") {
            Text("保养累计：¥${summary.maintenanceCostCents / 100.0}")
            Text("维修累计：¥${summary.repairCostCents / 100.0}")
            Text("总计：¥${(summary.maintenanceCostCents + summary.repairCostCents) / 100.0}")
        }
        Button(onClick = onAddMaintenance, modifier = Modifier.fillMaxWidth()) {
            Text("新增保养记录")
        }
        Button(onClick = onAddRepair, modifier = Modifier.fillMaxWidth()) {
            Text("新增维修记录")
        }
    }
}

@Composable
private fun SummaryCard(title: String, content: @Composable () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(title)
            content()
        }
    }
}

private fun ReminderStatus.displayName(): String = when (this) {
    ReminderStatus.NO_RECORD -> "暂无保养记录"
    ReminderStatus.NOT_DUE -> "保养未到期"
    ReminderStatus.DUE_SOON -> "保养即将到期"
    ReminderStatus.OVERDUE -> "建议尽快保养"
}

private fun ReminderStatus.description(): String = when (this) {
    ReminderStatus.NO_RECORD -> "添加一条保养记录后，将按里程和日期计算提醒。"
    ReminderStatus.NOT_DUE -> "当前保养周期正常。"
    ReminderStatus.DUE_SOON -> "距上次保养已接近 5,000 km 或 180 天。"
    ReminderStatus.OVERDUE -> "距上次保养已达到 5,000 km 或 180 天。"
}
