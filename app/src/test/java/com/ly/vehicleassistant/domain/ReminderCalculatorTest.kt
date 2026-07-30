package com.ly.vehicleassistant.domain

import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.MaintenanceType
import com.ly.vehicleassistant.data.model.ReminderStatus
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class ReminderCalculatorTest {
    private val today = LocalDate.of(2026, 7, 30)

    @Test
    fun calculateReminderReturnsNoRecordWhenNoMaintenanceExists() {
        val status = calculateReminder(
            lastMaintenance = null,
            currentMileage = 20_000,
            today = today,
        )

        assertEquals(ReminderStatus.NO_RECORD, status)
    }

    @Test
    fun calculateReminderReturnsDueSoonAtEightyPercentMileage() {
        val status = calculateReminder(
            lastMaintenance = maintenanceRecord(
                date = LocalDate.of(2026, 6, 1),
                mileage = 10_000,
            ),
            currentMileage = 14_000,
            today = today,
        )

        assertEquals(ReminderStatus.DUE_SOON, status)
    }

    @Test
    fun calculateReminderReturnsOverdueAtMileageLimit() {
        val status = calculateReminder(
            lastMaintenance = maintenanceRecord(
                date = LocalDate.of(2026, 6, 1),
                mileage = 10_000,
            ),
            currentMileage = 15_000,
            today = today,
        )

        assertEquals(ReminderStatus.OVERDUE, status)
    }

    @Test
    fun calculateReminderReturnsOverdueAtDayLimit() {
        val status = calculateReminder(
            lastMaintenance = maintenanceRecord(
                date = LocalDate.of(2026, 1, 31),
                mileage = 10_000,
            ),
            currentMileage = 11_000,
            today = today,
        )

        assertEquals(ReminderStatus.OVERDUE, status)
    }

    private fun maintenanceRecord(date: LocalDate, mileage: Int) = MaintenanceRecord(
        id = 1,
        vehicleId = 1,
        date = date,
        mileage = mileage,
        type = MaintenanceType.ROUTINE,
        items = "Engine oil",
        costCents = 60_000,
        shopName = null,
        note = null,
        createdAt = Instant.EPOCH,
    )
}
