package com.ly.vehicleassistant.domain

import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.ReminderStatus
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private const val MAINTENANCE_INTERVAL_KM = 5_000
private const val MAINTENANCE_INTERVAL_DAYS = 180L
private const val DUE_SOON_PERCENT = 0.8
private const val DUE_SOON_KM = (MAINTENANCE_INTERVAL_KM * DUE_SOON_PERCENT).toInt()
private const val DUE_SOON_DAYS = (MAINTENANCE_INTERVAL_DAYS * DUE_SOON_PERCENT).toLong()

/**
 * 以最近一次保养记录为基准，按 5,000 km 或 180 天的任一条件计算提醒。
 * 周期达到 80% 时进入“即将到期”状态。
 */
fun calculateReminder(
    lastMaintenance: MaintenanceRecord?,
    currentMileage: Int,
    today: LocalDate,
): ReminderStatus {
    if (lastMaintenance == null) return ReminderStatus.NO_RECORD

    val mileageSinceMaintenance = (currentMileage - lastMaintenance.mileage).coerceAtLeast(0)
    val daysSinceMaintenance = ChronoUnit.DAYS
        .between(lastMaintenance.date, today)
        .coerceAtLeast(0)

    return when {
        mileageSinceMaintenance >= MAINTENANCE_INTERVAL_KM ||
            daysSinceMaintenance >= MAINTENANCE_INTERVAL_DAYS -> ReminderStatus.OVERDUE

        mileageSinceMaintenance >= DUE_SOON_KM ||
            daysSinceMaintenance >= DUE_SOON_DAYS -> ReminderStatus.DUE_SOON

        else -> ReminderStatus.NOT_DUE
    }
}
