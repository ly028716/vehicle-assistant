package com.ly.vehicleassistant.data.repository

import com.ly.vehicleassistant.data.local.entity.VehicleEntity
import com.ly.vehicleassistant.data.local.entity.MaintenanceRecordEntity
import com.ly.vehicleassistant.data.local.entity.RepairRecordEntity
import com.ly.vehicleassistant.data.model.FuelType
import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.MaintenanceRecord
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.RepairRecord
import com.ly.vehicleassistant.data.model.Vehicle
import java.time.Instant
import java.time.LocalDate

fun Vehicle.toEntity(): VehicleEntity = VehicleEntity(
    id = id,
    brand = brand,
    model = model,
    licensePlate = licensePlate,
    purchaseDateEpochDay = purchaseDate?.toEpochDay(),
    currentMileage = currentMileage,
    fuelType = fuelType.name,
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
)

fun VehicleEntity.toVehicle(): Vehicle = Vehicle(
    id = id,
    brand = brand,
    model = model,
    licensePlate = licensePlate,
    purchaseDate = purchaseDateEpochDay?.let(LocalDate::ofEpochDay),
    currentMileage = currentMileage,
    fuelType = FuelType.valueOf(fuelType),
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis),
)

fun MaintenanceInput.toEntity(vehicleId: Long, createdAt: Instant): MaintenanceRecordEntity = MaintenanceRecordEntity(
    vehicleId = vehicleId,
    dateEpochDay = dateEpochDay,
    mileage = mileage,
    type = type.name,
    items = items.trim(),
    costCents = costCents,
    shopName = shopName?.trim()?.ifBlank { null },
    note = note?.trim()?.ifBlank { null },
    createdAtEpochMillis = createdAt.toEpochMilli(),
)

fun MaintenanceRecordEntity.toMaintenanceRecord(): MaintenanceRecord = MaintenanceRecord(
    id = id,
    vehicleId = vehicleId,
    date = LocalDate.ofEpochDay(dateEpochDay),
    mileage = mileage,
    type = com.ly.vehicleassistant.data.model.MaintenanceType.valueOf(type),
    items = items,
    costCents = costCents,
    shopName = shopName,
    note = note,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
)

fun RepairInput.toEntity(vehicleId: Long, createdAt: Instant): RepairRecordEntity = RepairRecordEntity(
    vehicleId = vehicleId,
    dateEpochDay = dateEpochDay,
    mileage = mileage,
    symptom = symptom.trim(),
    repairItems = repairItems.trim(),
    costCents = costCents,
    shopName = shopName?.trim()?.ifBlank { null },
    note = note?.trim()?.ifBlank { null },
    createdAtEpochMillis = createdAt.toEpochMilli(),
)

fun RepairRecordEntity.toRepairRecord(): RepairRecord = RepairRecord(
    id = id,
    vehicleId = vehicleId,
    date = LocalDate.ofEpochDay(dateEpochDay),
    mileage = mileage,
    symptom = symptom,
    repairItems = repairItems,
    costCents = costCents,
    shopName = shopName,
    note = note,
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
)
