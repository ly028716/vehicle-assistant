package com.ly.vehicleassistant.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val brand: String,
    val model: String,
    val licensePlate: String?,
    val purchaseDateEpochDay: Long?,
    val currentMileage: Int,
    val fuelType: String,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)
