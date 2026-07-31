package com.ly.vehicleassistant.data.export

import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.Reminder
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle
import java.time.Instant

data class ExportSnapshot(
    val schemaVersion: Int = 1,
    val exportedAt: String = Instant.now().toString(),
    val vehicle: Vehicle? = null,
    val maintenanceRecords: List<MaintenanceRecord> = emptyList(),
    val repairRecords: List<RepairRecord> = emptyList(),
    val reminders: List<Reminder> = emptyList(),
)

interface VehicleDataExporter {
    suspend fun createJson(): Result<String>
}
