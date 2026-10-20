package com.ly.vehicleassistant.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.domain.calculateReminder
import com.ly.vehicleassistant.domain.repository.VehicleRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    repository: VehicleRepository,
    today: () -> LocalDate = LocalDate::now,
) : ViewModel() {
    val uiState: StateFlow<HomeSummary> = combine(
        repository.observeVehicle(),
        repository.observeMaintenanceRecords(),
        repository.observeRepairRecords(),
    ) { vehicle, maintenanceRecords, repairRecords ->
        vehicle.toHomeSummary(maintenanceRecords, repairRecords, today())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = HomeSummary(),
    )
}

private fun com.ly.vehicleassistant.data.model.Vehicle?.toHomeSummary(
    maintenanceRecords: List<MaintenanceRecord>,
    repairRecords: List<RepairRecord>,
    today: LocalDate,
): HomeSummary {
    val latestMaintenance = maintenanceRecords.maxByOrNull { it.date }
    val latestRepair = repairRecords.maxByOrNull { it.date }
    return HomeSummary(
        vehicle = this,
        reminderStatus = this?.let { calculateReminder(latestMaintenance, it.currentMileage, today) }
            ?: com.ly.vehicleassistant.data.model.ReminderStatus.NO_RECORD,
        latestMaintenance = latestMaintenance,
        latestRepair = latestRepair,
        maintenanceCount = maintenanceRecords.size,
        repairCount = repairRecords.size,
        maintenanceCostCents = maintenanceRecords.sumOf { it.costCents },
        repairCostCents = repairRecords.sumOf { it.costCents },
    )
}
