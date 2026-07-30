package com.ly.vehicleassistant.data.repository

import com.ly.vehicleassistant.data.local.entity.VehicleEntity
import com.ly.vehicleassistant.data.model.FuelType
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
