package com.ly.vehicleassistant.ui.home

import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.ReminderStatus
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle

data class HomeSummary(
    val vehicle: Vehicle? = null,
    val reminderStatus: ReminderStatus = ReminderStatus.NO_RECORD,
    val latestMaintenance: MaintenanceRecord? = null,
    val latestRepair: RepairRecord? = null,
    val maintenanceCount: Int = 0,
    val repairCount: Int = 0,
    val maintenanceCostCents: Long = 0,
    val repairCostCents: Long = 0,
) {
    val recordCount: Int
        get() = maintenanceCount + repairCount

    val totalCostCents: Long
        get() = maintenanceCostCents + repairCostCents
}
