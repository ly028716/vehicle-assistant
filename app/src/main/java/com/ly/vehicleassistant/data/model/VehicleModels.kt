package com.ly.vehicleassistant.data.model

import java.time.Instant
import java.time.LocalDate

/** 车辆能源类型。 */
enum class FuelType {
    GASOLINE,
    DIESEL,
    ELECTRIC,
    HYBRID,
    OTHER,
}

/** 保养类型。 */
enum class MaintenanceType {
    ROUTINE,
    MAJOR,
    ENGINE_OIL,
    BRAKE_PAD,
    TIRE,
    BATTERY,
    AIR_CONDITIONING,
    OTHER,
}

/** 保养提醒状态。 */
enum class ReminderStatus {
    NO_RECORD,
    NOT_DUE,
    DUE_SOON,
    OVERDUE,
}

/** 表单校验失败原因，供界面层映射为用户可读提示。 */
sealed interface ValidationError {
    data object BrandRequired : ValidationError

    data object ModelRequired : ValidationError

    data object MileageNegative : ValidationError

    data object MileageBelowRecordedMileage : ValidationError

    data object CostNegative : ValidationError

    data object DateInFuture : ValidationError
}

data class Vehicle(
    val id: Long,
    val brand: String,
    val model: String,
    val licensePlate: String?,
    val purchaseDate: LocalDate?,
    val currentMileage: Int,
    val fuelType: FuelType,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class VehicleInput(
    val brand: String,
    val model: String,
    val licensePlate: String? = null,
    val purchaseDate: LocalDate? = null,
    val currentMileage: Int,
    val fuelType: FuelType,
)

data class MaintenanceRecord(
    val id: Long,
    val vehicleId: Long,
    val date: LocalDate,
    val mileage: Int,
    val type: MaintenanceType,
    val items: String,
    val costCents: Long,
    val shopName: String?,
    val note: String?,
    val createdAt: Instant,
)

data class RepairRecord(
    val id: Long,
    val vehicleId: Long,
    val date: LocalDate,
    val mileage: Int,
    val symptom: String,
    val repairItems: String,
    val costCents: Long,
    val shopName: String?,
    val note: String?,
    val createdAt: Instant,
)

data class Reminder(
    val status: ReminderStatus,
    val lastMaintenanceId: Long?,
    val mileageUntilDue: Int?,
    val daysUntilDue: Long?,
)
