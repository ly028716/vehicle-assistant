package com.ly.vehicleassistant.domain.repository

import com.ly.vehicleassistant.data.model.MaintenanceInput
import com.ly.vehicleassistant.data.model.RepairInput
import com.ly.vehicleassistant.data.model.Vehicle
import com.ly.vehicleassistant.data.model.VehicleInput
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {
    fun observeVehicle(): Flow<Vehicle?>

    suspend fun createVehicle(input: VehicleInput): Result<Long>

    suspend fun updateVehicle(input: VehicleInput): Result<Unit>

    suspend fun saveMaintenance(input: MaintenanceInput): Result<Long>

    suspend fun saveRepair(input: RepairInput): Result<Long>

    suspend fun deleteAllVehicleData(): Result<Unit>
}
