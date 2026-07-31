package com.ly.vehicleassistant.data.export

import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.Reminder
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.domain.repository.VehicleRepository

class JsonVehicleDataExporter(
    private val repository: VehicleRepository,
) : VehicleDataExporter {
    override suspend fun createJson(): Result<String> = runCatching {
        repository.exportSnapshot().toJson()
    }
}

private fun ExportSnapshot.toJson(): String = buildString {
    append('{')
    append("\"schemaVersion\":").append(schemaVersion).append(',')
    append("\"exportedAt\":").appendJsonString(exportedAt).append(',')
    append("\"vehicle\":").append(vehicle?.toJson() ?: "null").append(',')
    append("\"maintenanceRecords\":").append(maintenanceRecords.toJsonArray { it.toJson() }).append(',')
    append("\"repairRecords\":").append(repairRecords.toJsonArray { it.toJson() }).append(',')
    append("\"reminders\":").append(reminders.toJsonArray { it.toJson() })
    append('}')
}

private fun Vehicle.toJson(): String = buildString {
    append('{')
    append("\"brand\":").appendJsonString(brand).append(',')
    append("\"model\":").appendJsonString(model).append(',')
    append("\"purchaseDate\":").appendJsonStringOrNull(purchaseDate?.toString()).append(',')
    append("\"currentMileage\":").append(currentMileage).append(',')
    append("\"fuelType\":").appendJsonString(fuelType.name)
    append('}')
}

private fun MaintenanceRecord.toJson(): String = buildString {
    append('{')
    append("\"date\":").appendJsonString(date.toString()).append(',')
    append("\"mileage\":").append(mileage).append(',')
    append("\"type\":").appendJsonString(type.name).append(',')
    append("\"items\":").appendJsonString(items).append(',')
    append("\"costCents\":").append(costCents).append(',')
    append("\"shopName\":").appendJsonStringOrNull(shopName).append(',')
    append("\"note\":").appendJsonStringOrNull(note)
    append('}')
}

private fun RepairRecord.toJson(): String = buildString {
    append('{')
    append("\"date\":").appendJsonString(date.toString()).append(',')
    append("\"mileage\":").append(mileage).append(',')
    append("\"symptom\":").appendJsonString(symptom).append(',')
    append("\"repairItems\":").appendJsonString(repairItems).append(',')
    append("\"costCents\":").append(costCents).append(',')
    append("\"shopName\":").appendJsonStringOrNull(shopName).append(',')
    append("\"note\":").appendJsonStringOrNull(note)
    append('}')
}

private fun Reminder.toJson(): String = buildString {
    append('{')
    append("\"status\":").appendJsonString(status.name).append(',')
    append("\"lastMaintenanceId\":").append(lastMaintenanceId ?: "null").append(',')
    append("\"mileageUntilDue\":").append(mileageUntilDue ?: "null").append(',')
    append("\"daysUntilDue\":").append(daysUntilDue ?: "null")
    append('}')
}

private fun <T> List<T>.toJsonArray(item: (T) -> String): String =
    joinToString(prefix = "[", postfix = "]", transform = item)

private fun StringBuilder.appendJsonStringOrNull(value: String?): StringBuilder {
    return if (value == null) append("null") else appendJsonString(value)
}

private fun StringBuilder.appendJsonString(value: String): StringBuilder {
    append('"')
    value.forEach { character ->
        when (character) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> append(character)
        }
    }
    append('"')
    return this
}
